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
        String message = event.getMessage();

        if (message.startsWith("/")) {
            return;
        }
        this.sender.post(this.plugin.getInstance().factory.makeChatMessage(player, message));
    }
}
