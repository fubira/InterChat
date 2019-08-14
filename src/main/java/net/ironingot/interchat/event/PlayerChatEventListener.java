package net.ironingot.interchat.event;

import net.ironingot.interchat.InterChatPlugin;
import net.ironingot.interchat.storage.IMessageStoreSender;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.HashMap;

public class PlayerChatEventListener implements Listener {
    public InterChatPlugin plugin;
    public IMessageStoreSender sender;

    public PlayerChatEventListener(InterChatPlugin plugin, IMessageStoreSender sender) {
        this.plugin = plugin;
        this.sender = sender;
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
        data.put("color", this.plugin.getConfigHandler().getServerColor());
        data.put("message", event.getMessage());
        this.sender.post(data);
    }
}
