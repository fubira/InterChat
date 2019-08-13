package net.ironingot.crossserverchat;

import org.bukkit.scheduler.BukkitRunnable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.lettuce.core.RedisClient;
import io.lettuce.core.Range;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisSortedSetCommands;
import io.lettuce.core.api.sync.RedisListCommands;

public class RedisChatStorage implements IChatStorage {
    private CrossServerChat plugin;

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;
    private String key = "logs";
    private long lastTime;

    public RedisChatStorage(CrossServerChat plugin) {
        this.plugin = plugin;
        this.redisClient = null;
    }

    public void open() {
        if (this.redisClient == null) {
            this.redisClient = RedisClient.create(this.plugin.getConfigHandler().getRedisURI());
            this.redisConnection = this.redisClient.connect();
            this.lastTime = System.currentTimeMillis();
        }
    }

    public void close() {
        if (this.redisConnection != null) {
            // this.redisConnection.close();
            this.redisConnection = null;
        }
        if (this.redisClient != null) {
            this.redisClient.shutdown();
            this.redisClient = null;
        }
    }

    public void post(final Map<String, Object> data) {
        if (this.redisConnection == null) {
            CrossServerChat.logger.warning("CrossServerChat post failed: redisConnection is close.");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                postMessage(data);
            }
        }.runTaskAsynchronously(this.plugin);
    }

    public void receive(final IChatReceiveCallback callback) {
        if (this.redisConnection == null) {
            CrossServerChat.logger.warning("CrossServerChat receive failed: redisConnection is close.");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                receiveMessage(callback);
            }
        }.runTask(this.plugin);
    }

    protected void postMessage(final Map<String, Object> data) {
        final String jsonString = new JSONObject(data).toString();
        final long time = System.currentTimeMillis();

        try {
            final RedisSortedSetCommands<String, String> sync = this.redisConnection.sync();
            sync.zadd(key, time, jsonString);
            // CrossServerChat.logger.info("Post: " + key + ", " + time + ", " + jsonString);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void receiveMessage(final IChatReceiveCallback callback) {
        final RedisSortedSetCommands<String, String> sync = this.redisConnection.sync();
        List<ScoredValue<String>> scoredValue = sync.zrangebyscoreWithScores(key, Range.create(this.lastTime, Double.POSITIVE_INFINITY));

        for (ScoredValue<String> value: scoredValue) {
            try {
                // CrossServerChat.logger.info("Receive: " + key + ", " + this.lastTime + ", " + value.getScore() + ":" + value.getValue());
                callback.message(new JSONObject(value.getValue()).toMap());
                this.lastTime = (long)value.getScore() + 1;
            }
            catch (JSONException e) {
            }
        }
    }
}
