package net.ironingot.interchat.event;

import net.ironingot.interchat.InterChatPlugin;
import net.ironingot.interchat.message.IMessageSender;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveEventListener implements Listener {
    public InterChatPlugin plugin;
    public IMessageSender sender;
    public int playerCount;

    public PlayerJoinLeaveEventListener(InterChatPlugin plugin, IMessageSender sender) {
        this.plugin = plugin;
        this.sender = sender;
        this.playerCount = 0;
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.playerCount = plugin.getServer().getOnlinePlayers().size();
        this.sender.post(this.plugin.getMessenger().factory.system(event.getJoinMessage()));
    }

    @EventHandler()
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.playerCount = plugin.getServer().getOnlinePlayers().size();
        if (plugin.getServer().getOnlinePlayers().contains((event.getPlayer()))) {
            this.playerCount -= 1;
        }

        this.sender.post(this.plugin.getMessenger().factory.system(event.getQuitMessage()));
    }
}
