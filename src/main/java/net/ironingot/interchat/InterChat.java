package net.ironingot.interchat;

import net.ironingot.interchat.storage.IMessageBroadcastor;
import net.ironingot.interchat.storage.IMessageStoreReceiver;
import net.ironingot.interchat.storage.RedisMessageStore;
import net.ironingot.interchat.event.PlayerChatEventListener;
import net.ironingot.interchat.event.PlayerJoinLeaveEventListener;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;

import java.lang.StringBuilder;
import java.util.Map;
import java.util.logging.Logger;

public class InterChat implements IMessageBroadcastor {
    public static final Logger logger = Logger.getLogger("Minecraft");
    public InterChatPlugin plugin;

    public BukkitTask chatReceiveTask = null;
    public RedisMessageStore messageStore;
    public IgnoreList ignoreList;

    public InterChat(InterChatPlugin plugin) {
        this.plugin = plugin;
 
        this.messageStore = new RedisMessageStore(this.plugin);
        this.ignoreList = new IgnoreList(this.plugin);
        registerEvents();
    }

    public void enable() {
        this.messageStore.open();
        this.startReceiveTask();
    }

    public void disable() {
        this.plugin.getServer().getScheduler().cancelTasks(this.plugin);
        this.stopReceiveTask();
        this.messageStore.close();
    }

    protected void registerEvents() {
        this.plugin.getServer().getPluginManager().registerEvents(new PlayerChatEventListener(this.plugin, this.messageStore), this.plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveEventListener(this.plugin, this.messageStore), this.plugin);
    }

    protected void startReceiveTask() {
        if (chatReceiveTask != null) {
            chatReceiveTask.cancel();
        }

        final IMessageStoreReceiver messageStoreReceiver = this.messageStore;
        final IMessageBroadcastor messageBroadcastor = this;
        chatReceiveTask = new BukkitRunnable() {
            @Override
            public void run() {
                messageStoreReceiver.receive(messageBroadcastor);
            }
        }.runTaskTimer(plugin, 50, 40);
    }

    protected void stopReceiveTask() {
        if (chatReceiveTask != null) {
            chatReceiveTask.cancel();
            chatReceiveTask = null;
        }
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

        String str = builder.toString();
        for(Player player: this.plugin.getServer().getOnlinePlayers()) {
            if (!ignoreList.isIgnored(player, senderName)) {
                player.sendMessage(str);
            }
        }
        // this.plugin.getServer().broadcastMessage(builder.toString());
    }
}