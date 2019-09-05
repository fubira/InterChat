package net.ironingot.interchat.message;

import net.ironingot.interchat.InterChatPlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageFactory {
    InterChatPlugin plugin;

    public MessageFactory(InterChatPlugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, Object> message(Player player, String message) {
        String server = this.plugin.getConfigHandler().getServerIdentify();
        String color = this.plugin.getConfigHandler().getServerColor();
        String name = player.getName();
        UUID uuid = player.getUniqueId();
        String world = player.getWorld().getName();

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSystem", new Boolean(false));
        data.put("senderName", name);
        data.put("senderUUID", uuid);
        data.put("senderWorld", world);
        data.put("server", server);
        data.put("color", color);
        data.put("message", message);
        return data;
    }

    public Map<String, Object> system(String message) {
        String server = this.plugin.getConfigHandler().getServerIdentify();
        String color = this.plugin.getConfigHandler().getServerColor();
        int playerCount = this.plugin.getServer().getOnlinePlayers().size();
        Boolean useTotalPlayerCount = this.plugin.getConfigHandler().useTotalPlayerCount();

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSystem", new Boolean(true));
        data.put("server", server);
        data.put("color", color);
        data.put("message", message);
        if (useTotalPlayerCount) {
            data.put("players", new Integer(playerCount));
        }
        return data;
    }

    public Map<String, Object> systemServerStart() {
        String server = this.plugin.getConfigHandler().getServerIdentify();
        return system(ChatColor.GREEN + "Server " + server + " has started.");
    }

    public Map<String, Object> systemServerStop() {
        String server = this.plugin.getConfigHandler().getServerIdentify();
        return system(ChatColor.GREEN + "Server " + server + " has stopped.");
    }
}
