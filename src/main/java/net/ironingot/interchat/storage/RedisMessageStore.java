package net.ironingot.interchat.storage;

import net.ironingot.interchat.InterChatPlugin;
import net.ironingot.interchat.InterChat;

import io.lettuce.core.RedisClient;
import io.lettuce.core.Range;
import io.lettuce.core.RedisCommandTimeoutException;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisSortedSetCommands;


import java.util.List;
import java.util.Map;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.time.Duration;

import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;

public class RedisMessageStore implements IMessageStoreSender, IMessageStoreReceiver {
    private InterChatPlugin plugin;

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;
    private String key = "logs";
    private long expireMillis = 1000 * 60 * 10;
    private long lastTime;

    public RedisMessageStore(InterChatPlugin plugin) {
        this.plugin = plugin;
        this.redisClient = null;
    }

    public void open() {
        if (this.redisClient == null) {
            this.redisClient = RedisClient.create(this.plugin.getConfigHandler().getRedisURI());
            this.redisClient.setDefaultTimeout(Duration.ofSeconds(10));
            this.redisConnection = this.redisClient.connect();
            this.lastTime = System.currentTimeMillis();

            // 古いメッセージを削除しておく
            this.expireMessage();
        }
    }

    public void close() {
        final RedisClient closingRedisClient = this.redisClient ;
        final StatefulRedisConnection<String, String> closingRedisConnection = this.redisConnection;
        this.redisConnection = null;
        this.redisClient = null;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (closingRedisClient != null) {
                    closingRedisConnection.close();
                }
                if (closingRedisClient != null) {
                    closingRedisClient.shutdown();
                }
            }
        }.runTaskLater(this.plugin, 20);
    }

    // Implement: IMessageStorePost
    public void post(final Map<String, Object> data) {
        if (this.redisConnection == null) {
            InterChat.logger.warning("InterChat post failed: redisConnection is close.");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                postMessage(data);
            }
        }.runTaskAsynchronously(this.plugin);
    }

    // Implement: IMessageStoreReceive
    public void receive(final IMessageBroadcastor broadcastor) {
        if (this.redisConnection == null) {
            InterChat.logger.warning("InterChat receive failed: redisConnection is close.");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                receiveMessage(broadcastor);
            }
        }.runTask(this.plugin);
    }

    protected void expireMessage() {
        final long time = System.currentTimeMillis();
        final RedisSortedSetCommands<String, String> sync = this.redisConnection.sync();
        sync.zremrangebyscore(key, Range.create(0, time - expireMillis));
    }

    protected void postMessage(final Map<String, Object> data) {
        final String jsonString = new JSONObject(data).toString();
        final long time = System.currentTimeMillis();

        try {
            final RedisSortedSetCommands<String, String> sync = this.redisConnection.sync();
            sync.zadd(key, time, jsonString);
            // InterChat.logger.info("Post: " + key + ", " + time + ", " + jsonString);
        }
        catch (JSONException e) {}
        catch (RedisCommandTimeoutException e) {}
    }

    protected void receiveMessage(final IMessageBroadcastor broadcastor) {
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
