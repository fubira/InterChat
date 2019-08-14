package net.ironingot.interchat;

import net.ironingot.interchat.InterChatPlugin;
import net.ironingot.interchat.storage.IMessageReceiver;
import net.ironingot.interchat.storage.IMessageStore;
import net.ironingot.interchat.storage.RedisMessageStore;
import net.ironingot.interchat.event.PlayerChatEventListener;
import net.ironingot.interchat.event.PlayerJoinLeaveEventListener;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.StringBuilder;
import java.util.Map;
import java.util.logging.Logger;

public class InterChat implements IMessageReceiver {
    public static final Logger logger = Logger.getLogger("Minecraft");
    public InterChatPlugin plugin;

    public BukkitTask chatReceiveTask = null;
    public RedisMessageStore redisMessageStore;

    public InterChat(InterChatPlugin plugin) {
        this.plugin = plugin;
 
        this.redisMessageStore = new RedisMessageStore(this.plugin);
        registerEvents();
    }

    public void enable() {
        this.redisMessageStore.open();
        this.startReceiveTask();
    }

    public void disable() {
        this.plugin.getServer().getScheduler().cancelTasks(this.plugin);
        this.stopReceiveTask();
        this.redisMessageStore.close();
    }

    public IMessageStore getStorageInterface() {
        return this.redisMessageStore;
    }

    protected void registerEvents() {
        this.plugin.getServer().getPluginManager().registerEvents(new PlayerChatEventListener(this.plugin), this.plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveEventListener(this.plugin), this.plugin);
    }

    protected void startReceiveTask() {
        if (chatReceiveTask != null) {
            chatReceiveTask.cancel();
        }

        final IMessageReceiver messageReceiver = this;
        final IMessageStore messageStore = this.redisMessageStore;
        chatReceiveTask = new BukkitRunnable() {
            @Override
            public void run() {
                messageStore.receive(messageReceiver);
            }
        }.runTaskTimer(plugin, 50, 40);
    }

    protected void stopReceiveTask() {
        if (chatReceiveTask != null) {
            chatReceiveTask.cancel();
            chatReceiveTask = null;
        }
    }

    // implement: IMessageReceiver
    public void receive(Map<String, Object> data) {
        if (data == null) {
            return;
        }

        final StringBuilder builder = new StringBuilder();
        Boolean isSystem = (Boolean) data.get("isSystem");
        String senderName = (String) data.get("senderName");
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

        builder.append(ChatColor.DARK_GRAY).append("[")
            .append(serverColor).append(server)
            .append(ChatColor.DARK_GRAY).append("]")
            .append(ChatColor.RESET).append(" ");
        if (!isSystem) {
            builder.append("<").append(senderName).append("> ");
        }
        builder.append(message);

        this.plugin.getServer().broadcastMessage(builder.toString());
    }
}
