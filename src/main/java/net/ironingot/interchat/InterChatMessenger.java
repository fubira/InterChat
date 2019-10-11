package net.ironingot.interchat;

import net.ironingot.interchat.InterChatPlugin;
import net.ironingot.interchat.message.IMessageBroadcastor;
import net.ironingot.interchat.message.IMessageSender;
import net.ironingot.interchat.message.MessageFactory;
import net.ironingot.interchat.message.IgnoreList;
import net.ironingot.interchat.storage.RedisMessageStore;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;

import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Map;

public class InterChatMessenger implements IMessageSender, IMessageBroadcastor {
    public InterChatPlugin plugin;

    public Backend backend;
    public BukkitTask chatReceiveTask = null;
    public RedisMessageStore messageStore;
    public MessageFactory factory;
    public IgnoreList ignoreList;
    public Map<String, Integer> externalPlayerCountMap = new HashMap<String, Integer>();

    public InterChatMessenger(InterChatPlugin plugin) {
        this.plugin = plugin;
        
        this.backend = new Backend(
            this.plugin.getConfigHandler().getBackendUrl(),
            this.plugin.getConfigHandler().getBackendAuthKey()
        );

        this.messageStore = new RedisMessageStore();
        this.factory = new MessageFactory(this.plugin);
        this.ignoreList = new IgnoreList(this.plugin);
    }

    public void enable() {
        // this.messageStore.open(this.plugin.getConfigHandler().getRedisURI());
        this.startReceiveTask();
        this.post(this.factory.systemServerStart());
    }

    public void disable() {
        this.post(this.factory.systemServerStop());
        this.stopReceiveTask();
        this.plugin.getServer().getScheduler().cancelTasks(this.plugin);
        // this.messageStore.close();
    }

    protected int getTotalExternalPlayers() {
        int count = 0;
        for (String key: this.externalPlayerCountMap.keySet()) {
            if (this.externalPlayerCountMap.containsKey(key)) {
                count = count + this.externalPlayerCountMap.get(key);
            }
        }
        return count;
    }

    public int getTotalPlayers() {
        return this.plugin.getServer().getOnlinePlayers().size() + this.getTotalExternalPlayers();
    }

    protected void startReceiveTask() {
        if (chatReceiveTask != null) {
            chatReceiveTask.cancel();
        }

        final IMessageBroadcastor broadcastor = this;
        chatReceiveTask = new BukkitRunnable() {
            @Override
            public void run() {
                backend.broadcastMessage(broadcastor);
                backend.receiveMessageAsync();
            }
        }.runTaskTimer(this.plugin, 100, 60);
    }

    protected void stopReceiveTask() {
        if (chatReceiveTask != null) {
            chatReceiveTask.cancel();
            chatReceiveTask = null;
        }
    }

    // implement: IMessageSender
    public void post(final Map<String, Object> data) {
        // this.messageStore.postMessage(data);
        backend.postMessageAsync(data);
    }

    // implement: IMessageBroadcastor
    public void broadcast(Map<String, Object> data) {
        if (data == null) {
            return;
        }

        final StringBuilder builder = new StringBuilder();
        Boolean isSystem = (Boolean) data.get("isSystem");
        String senderName = (String) data.get("senderName");
        String message = (String) data.get("message");
        String server = (String) data.get("server");
        String color = (String) data.get("color");
        Integer playersCount = (Integer) data.get("players");

        if (this.plugin.getConfigHandler().getServerIdentify().equals(server)) {
            return;
        }

        if (server == null && senderName == null && message == null) {
            InterChatPlugin.logger.warning("broadcast failed: empty message received.");
            return;
        }

        if (playersCount != null) {
            externalPlayerCountMap.put(server, playersCount);
        }

        ChatColor serverColor = ChatColor.GRAY;
        if (color != null) {
            try {
                serverColor = ChatColor.valueOf(color);
            } catch (IllegalArgumentException e) {}
        }

        builder
            .append(ChatColor.DARK_GRAY).append("[")
            .append(serverColor).append(server)
            .append(ChatColor.DARK_GRAY).append("]")
            .append(ChatColor.RESET).append(" ");

        if (!isSystem) {
            builder.append("<").append(senderName).append("> ");
        }
        builder.append(message);

        final String str = builder.toString();
        for(Player player: this.plugin.getServer().getOnlinePlayers()) {
            if (!ignoreList.isIgnored(player, senderName)) {
                player.sendMessage(str);
            }
        }
    }
}
