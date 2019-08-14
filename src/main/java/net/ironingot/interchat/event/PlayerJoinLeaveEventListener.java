package net.ironingot.interchat.event;

import net.ironingot.interchat.InterChatPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.HashMap;

public class PlayerJoinLeaveEventListener implements Listener {
    public InterChatPlugin plugin;

    public PlayerJoinLeaveEventListener(InterChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSystem", new Boolean(true));
        data.put("server", this.plugin.getConfigHandler().getServerIdentify());
        data.put("color", this.plugin.getConfigHandler().getServerColor());
        data.put("message", event.getJoinMessage());
        this.plugin.getInstance().getStorageInterface().post(data);
    }

    @EventHandler()
    public void onPlayerLogout(PlayerQuitEvent event) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSystem", new Boolean(true));
        data.put("server", this.plugin.getConfigHandler().getServerIdentify());
        data.put("color", this.plugin.getConfigHandler().getServerColor());
        data.put("message", event.getQuitMessage());
        this.plugin.getInstance().getStorageInterface().post(data);
    }
}
