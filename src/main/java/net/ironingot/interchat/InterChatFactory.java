package net.ironingot.interchat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InterChatFactory {
    InterChatPlugin plugin;

    public InterChatFactory(InterChatPlugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, Object> makeChatMessage(Player player, String message) {
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

    public Map<String, Object> makeSystemMessage(String message, int players) {
        String server = this.plugin.getConfigHandler().getServerIdentify();
        String color = this.plugin.getConfigHandler().getServerColor();
        Boolean useTotalPlayerCount = this.plugin.getConfigHandler().useTotalPlayerCount();

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSystem", new Boolean(true));
        data.put("server", server);
        data.put("color", color);
        data.put("message", message);
        if (useTotalPlayerCount) {
            data.put("players", new Integer(players));
        }
        return data;
    }

    public Map<String, Object> makeSystemMessage(String message) {
        return makeSystemMessage(message, 0);
    }

    public Map<String, Object> makeServerStartMessage() {
        String server = this.plugin.getConfigHandler().getServerIdentify();
        return makeSystemMessage(ChatColor.GREEN + "Server " + server + " has started.");
    }

    public Map<String, Object> makeServerStopMessage() {
        String server = this.plugin.getConfigHandler().getServerIdentify();
        return makeSystemMessage(ChatColor.GREEN + "Server " + server + " has stopped.");
    }
}
