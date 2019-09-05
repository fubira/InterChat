package net.ironingot.interchat.storage;

import net.ironingot.interchat.message.IMessageBroadcastor;

import io.lettuce.core.RedisClient;
import io.lettuce.core.Range;
import io.lettuce.core.RedisCommandTimeoutException;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisSortedSetCommands;

import java.util.List;
import java.util.Map;
import java.time.Duration;

import org.json.JSONException;
import org.json.JSONObject;

public class RedisMessageStore {
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;
    private String key = "logs";
    private long expireMillis = 1000 * 60 * 10;
    private long lastTime;

    public RedisMessageStore() {}

    public void open(String uri) {
        if (this.redisClient != null) {
            close();
        }

        this.redisClient = RedisClient.create(uri);
        this.redisClient.setDefaultTimeout(Duration.ofSeconds(10));
        this.redisConnection = this.redisClient.connect();
        this.lastTime = System.currentTimeMillis();

        this.expireMessage();
    }

    public void close() {
        final RedisClient closingRedisClient = this.redisClient;
        final StatefulRedisConnection<String, String> closingRedisConnection = this.redisConnection;
        this.redisConnection = null;
        this.redisClient = null;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (closingRedisClient != null) {
                    closingRedisConnection.close();
                }
                if (closingRedisClient != null) {
                    closingRedisClient.shutdown();
                }
            }
        }).start();
    }

    protected void expireMessage() {
        final long time = System.currentTimeMillis();
        final RedisSortedSetCommands<String, String> sync = this.redisConnection.sync();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sync.zremrangebyscore(key, Range.create(0, time - expireMillis));
                }
                catch (JSONException e) {}
                catch (RedisCommandTimeoutException e) {}
            }
        }).start();
    }

    public void postMessage(final Map<String, Object> data) {
        if (this.redisConnection == null) {
            return;
        }

        final String jsonString = new JSONObject(data).toString();
        final long time = System.currentTimeMillis();
        final RedisSortedSetCommands<String, String> sync = this.redisConnection.sync();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sync.zadd(key, time, jsonString);
                    // net.ironingot.interchat.InterChatPlugin.logger.info("Post: " + key + ", " + time + ", " + jsonString);
                }
                catch (JSONException e) {}
                catch (RedisCommandTimeoutException e) {}
            }
        }).start();
    }

    public void receiveMessage(final IMessageBroadcastor broadcastor) {
        if (this.redisConnection == null) {
            return;
        }

        try {
            final RedisSortedSetCommands<String, String> sync = this.redisConnection.sync();
            List<ScoredValue<String>> scoredValue = sync.zrangebyscoreWithScores(key, Range.create(this.lastTime, Double.POSITIVE_INFINITY));
            for (ScoredValue<String> value: scoredValue) {
                try {
                    // InterChat.logger.info("Receive: " + key + ", " + this.lastTime + ", " + value.getScore() + ":" + value.getValue());
                    broadcastor.broadcast(new JSONObject(value.getValue()).toMap());
                    this.lastTime = (long)value.getScore() + 1;
                }
                catch (JSONException e) {}
            }
        }
        catch (RedisCommandTimeoutException e) {}
    }
}
