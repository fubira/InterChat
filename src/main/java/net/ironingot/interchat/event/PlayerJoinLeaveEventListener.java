package net.ironingot.interchat.event;

import net.ironingot.interchat.InterChatPlugin;
import net.ironingot.interchat.storage.IMessageStoreSender;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.HashMap;

public class PlayerJoinLeaveEventListener implements Listener {
    public InterChatPlugin plugin;
    public IMessageStoreSender sender;

    public PlayerJoinLeaveEventListener(InterChatPlugin plugin, IMessageStoreSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSystem", new Boolean(true));
        data.put("server", this.plugin.getConfigHandler().getServerIdentify());
        data.put("color", this.plugin.getConfigHandler().getServerColor());
        data.put("message", event.getJoinMessage());
        this.sender.post(data);
    }

    @EventHandler()
    public void onPlayerLeave(PlayerQuitEvent event) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSystem", new Boolean(true));
        data.put("server", this.plugin.getConfigHandler().getServerIdentify());
        data.put("color", this.plugin.getConfigHandler().getServerColor());
        data.put("message", event.getQuitMessage());
        this.sender.post(data);
    }
}
