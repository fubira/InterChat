package net.ironingot.interchat.event;

import net.ironingot.interchat.InterChatPlugin;
import net.ironingot.interchat.message.IMessageSender;

import org.bukkit.ChatColor;
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
        InterChatPlugin.logger.info(event.getEventName() + ',' + event.getPlayer().getName() + ',' + event.getJoinMessage());
        this.playerCount = plugin.getServer().getOnlinePlayers().size();

        final StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.YELLOW).append(event.getPlayer().getName()).append(" joined the game");
        String message = !event.getJoinMessage().isEmpty() ? event.getJoinMessage() :  builder.toString();
        this.sender.post(this.plugin.getMessenger().factory.system(message));
    }

    @EventHandler()
    public void onPlayerLeave(PlayerQuitEvent event) {
        InterChatPlugin.logger.info(event.getEventName() + ',' + event.getPlayer().getName() + ',' + event.getQuitMessage());
        this.playerCount = plugin.getServer().getOnlinePlayers().size();
        if (plugin.getServer().getOnlinePlayers().contains((event.getPlayer()))) {
            this.playerCount -= 1;
        }

        final StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.YELLOW).append(event.getPlayer().getName()).append(" left the game");
        String message = !event.getQuitMessage().isEmpty() ? event.getQuitMessage() : builder.toString();
        this.sender.post(this.plugin.getMessenger().factory.system(message));
    }
}
