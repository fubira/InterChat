package net.ironingot.interchat;

import org.bukkit.entity.Player;
import com.gmail.fyrvelm.ChatCo.ChatCo;
import java.io.IOException;

class IgnoreList {
    private InterChatPlugin plugin;

    public IgnoreList(InterChatPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
    }

    public boolean isIgnored(Player player, String fromName) {
        return testChatCoIgnored(player, fromName);
    }

    protected boolean testChatCoIgnored(Player player, String fromName) {
        ChatCo chatco = (ChatCo) this.plugin.getServer().getPluginManager().getPlugin("ChatCo");
        boolean ignored = false;

        try {
            if (chatco != null) {
                ignored = chatco.getCCPlayer(player).isIgnored(fromName);
                // InterChat.logger.info("from: " + fromName + " to: " + player.getName() + " ignored: " + ignored);
            }
        } catch (IOException e) {}
        
        return ignored;
    }
}