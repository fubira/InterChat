package net.ironingot.crossserverchat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.StringBuilder;
import java.util.Map;
import java.util.HashMap;

public class PlayerEventListener implements Listener, IChatReceiveCallback {
    public CrossServerChat plugin;
    public BukkitTask chatReceiveTask = null;

    public PlayerEventListener(CrossServerChat plugin) {
        this.plugin = plugin;
    }

    public void startReceive() {
        if (chatReceiveTask != null) {
            chatReceiveTask.cancel();
        }
        CrossServerChat.logger.info("startReceive");

        final IChatReceiveCallback callback = this;
        final IChatStorage chatStorage = this.plugin.getChatStorage();
        chatReceiveTask = new BukkitRunnable() {
            @Override
            public void run() {
                chatStorage.receive(callback);
            }
        }.runTaskTimer(plugin, 50, 50);
    }

    public void stopReceive() {
        CrossServerChat.logger.info("stopReceive");
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

        if (isSystem) {
            String message = (String) data.get("message");
            String server = (String) data.get("server");

            builder.append(ChatColor.DARK_GREEN).append("[").append(server)
                .append("]").append(ChatColor.RESET).append(" ").append(message);

        } else {
            String senderName = (String) data.get("senderName");
            String senderWorld = (String) data.get("senderWorld");
            String server = (String) data.get("server");
            String message = (String) data.get("message");
            // String format = (String) data.get("format");

            builder.append(ChatColor.DARK_GREEN).append("[").append(server)
                .append("@").append(senderWorld).append("]").append(ChatColor.RESET)
                .append("<").append(senderName).append("> ").append(message);
        }
        this.plugin.getServer().broadcastMessage(builder.toString());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (event.getMessage().startsWith("/")) {
            return;
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSystem", new Boolean(false));
        data.put("senderName", player.getName());
        data.put("senderUUID", player.getUniqueId());
        data.put("senderWorld", player.getWorld().getName());
        data.put("server", this.plugin.getConfigHandler().getServerIdentify());
        data.put("message", event.getMessage());
        data.put("format", event.getFormat());
        this.plugin.getChatStorage().post(data);
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSystem", new Boolean(true));
        data.put("server", this.plugin.getConfigHandler().getServerIdentify());
        data.put("message", event.getJoinMessage());
        this.plugin.getChatStorage().post(data);
    }

    @EventHandler()
    public void onPlayerLogout(PlayerQuitEvent event) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSystem", new Boolean(true));
        data.put("server", this.plugin.getConfigHandler().getServerIdentify());
        data.put("message", event.getQuitMessage());
        this.plugin.getChatStorage().post(data);
    }
}
