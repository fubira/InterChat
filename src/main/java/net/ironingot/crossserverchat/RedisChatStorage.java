package net.ironingot.crossserverchat;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import redis.clients.jedis.Jedis;

public class RedisChatStorage implements IChatStorage {
    private CrossServerChat plugin;

    private Jedis jedis;
    private String key = "logs";
    private long lastReadTime;

    public RedisChatStorage(CrossServerChat plugin) {
        this.plugin = plugin;
        this.jedis = null;
    }

    public void open() {
        if (this.jedis == null) {
            this.jedis = new Jedis(this.plugin.getConfigHandler().getRedisURI());
            this.lastReadTime = System.currentTimeMillis();
        }
    }

    public void close() {
        this.jedis.close();
        this.jedis = null;
    }

    public void post(final Map<String, Object> data) {
        if (this.jedis == null) {
            CrossServerChat.logger.warning("CrossServerChat posting failed: redis is close.");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                postMessage(data);
            }
        }.runTask(this.plugin);
    }

    public void receive(final IChatReceiveCallback callback) {
        if (this.jedis == null) {
            CrossServerChat.logger.warning("CrossServerChat receiving failed: redis is close.");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                receiveMessage(callback);
            }
        }.runTask(this.plugin);
    }

    protected synchronized void postMessage(final Map<String, Object> data) {
        String jsonString = new JSONObject(data).toString();
        CrossServerChat.logger.info("JsonString: " + jsonString);

        try {
            long time = System.currentTimeMillis();
            this.jedis.zadd(key, time, jsonString);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected synchronized void receiveMessage(IChatReceiveCallback callback) {
        Set<String> messages = this.receiveNewMessages();
        if (messages == null) {
            return;
        }

        for (String message: messages) {
            try {
                callback.message(new JSONObject(message).toMap());
            }
            catch (JSONException e) {
            }
        }
    }

    protected Set<String> receiveNewMessages() {
        long fromTime = this.lastReadTime;
        long toTime = System.currentTimeMillis();

        Set<String> logs = null;
        try {
            logs = this.jedis.zrangeByScore(key, fromTime, toTime);
            this.lastReadTime = toTime;
        }
        catch (JSONException e) {
        }

        return logs;
    }
}
