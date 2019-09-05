package net.ironingot.interchat.event;

import net.ironingot.interchat.InterChatPlugin;
import net.ironingot.interchat.storage.IMessageStoreSender;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerJoinLeaveEventListener implements Listener {
    public InterChatPlugin plugin;
    public IMessageStoreSender sender;
    public int playerCount;

    public PlayerJoinLeaveEventListener(InterChatPlugin plugin, IMessageStoreSender sender) {
        this.plugin = plugin;
        this.sender = sender;
        this.playerCount = 0;
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.playerCount = plugin.getServer().getOnlinePlayers().size();
        this.sender.post(this.plugin.getInstance().factory.makeSystemMessage(event.getJoinMessage(), this.playerCount));
    }

    @EventHandler()
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.playerCount = plugin.getServer().getOnlinePlayers().size();
        if (plugin.getServer().getOnlinePlayers().contains((event.getPlayer()))) {
            this.playerCount -= 1;
        }

        Map<String, Object> data = new HashMap<String, Object>();
        this.sender.post(this.plugin.getInstance().factory.makeSystemMessage(event.getQuitMessage(), this.playerCount));
    }
}
