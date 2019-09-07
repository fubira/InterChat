package net.ironingot.interchat.event;

import net.ironingot.interchat.InterChatPlugin;
import net.ironingot.interchat.message.IMessageSender;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathEventListener implements Listener {
    public InterChatPlugin plugin;
    public IMessageSender sender;

    public PlayerDeathEventListener(InterChatPlugin plugin, IMessageSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    @EventHandler()
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.sender.post(this.plugin.getMessenger().factory.system(event.getDeathMessage()));
    }
}
