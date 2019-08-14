package net.ironingot.interchat;

import net.ironingot.interchat.InterChatPlugin;
import net.ironingot.interchat.interfaces.IChatReceiveCallback;
import net.ironingot.interchat.interfaces.IChatStorage;
import net.ironingot.interchat.storage.RedisChatStorage;
import net.ironingot.interchat.event.PlayerChatEventListener;
import net.ironingot.interchat.event.PlayerJoinLeaveEventListener;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.StringBuilder;
import java.util.Map;

public class InterChat implements IChatReceiveCallback {
    public InterChatPlugin plugin;
    public BukkitTask chatReceiveTask = null;
    public RedisChatStorage chatStorage;

    public InterChat(InterChatPlugin plugin) {
        this.plugin = plugin;
        this.chatStorage = new RedisChatStorage(this.plugin);
        registerEvents();
    }

    public void enable() {
        this.chatStorage.open();
        this.startReceiveTask();
    }

    public void disable() {
        this.plugin.getServer().getScheduler().cancelTasks(this.plugin);
        this.stopReceiveTask();
        this.chatStorage.close();
    }

    public IChatStorage getStorageInterface() {
        return this.chatStorage;
    }

    protected void registerEvents() {
        this.plugin.getServer().getPluginManager().registerEvents(new PlayerChatEventListener(this.plugin), this.plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveEventListener(this.plugin), this.plugin);
    }

    protected void startReceiveTask() {
        if (chatReceiveTask != null) {
            chatReceiveTask.cancel();
        }

        final IChatReceiveCallback callback = this;
        final IChatStorage chatStorage = this.chatStorage;
        chatReceiveTask = new BukkitRunnable() {
            @Override
            public void run() {
                chatStorage.receive(callback);
            }
        }.runTaskTimer(plugin, 50, 30);
    }

    protected void stopReceiveTask() {
        if (chatReceiveTask != null) {
            chatReceiveTask.cancel();
            chatReceiveTask = null;
        }
    }

    public void message(Map<String, Object> data) {
        if (data == null) {
            return;
        }

        final Boolean isSystem = (Boolean) data.get("isSystem");
        final StringBuilder builder = new StringBuilder();
        String message = (String) data.get("message");
        String server = (String) data.get("server");
        String color = (String) data.get("color");

        if (this.plugin.getConfigHandler().getServerIdentify().equals(server)) {
            return;
        }

        ChatColor serverColor = ChatColor.GRAY;
        if (color != null) {
            try {
                serverColor = ChatColor.valueOf(color);
            } catch (IllegalArgumentException e) {}
        }

        if (isSystem) {
            builder.append(ChatColor.DARK_GRAY).append("[")
                .append(serverColor).append(server)
                .append(ChatColor.DARK_GRAY).append("]")
                .append(ChatColor.RESET).append(" ")
                .append(message);

        } else {
            String senderName = (String) data.get("senderName");
            builder.append(ChatColor.DARK_GRAY).append("[")
                .append(serverColor).append(server)
                .append(ChatColor.DARK_GRAY).append("]")
                .append(ChatColor.RESET)
                .append("<").append(senderName).append("> ")
                .append(message);
        }
        this.plugin.getServer().broadcastMessage(builder.toString());
    }
}
