package net.ironingot.interchat;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

import com.gmail.fyrvelm.ChatCo.ChatCo;

import java.io.IOException;

class IgnoreList {
    private InterChatPlugin plugin;
    private ChatCo chatco = null;

    public IgnoreList(InterChatPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
        Plugin chatcoPlugin = this.plugin.getServer().getPluginManager().getPlugin("ChatCo");
        if (chatcoPlugin instanceof ChatCo) {
            InterChat.logger.info("InterChat: ChatCo found. using ignorelist.");
            this.chatco = (ChatCo) chatcoPlugin;
        }

    }

    public boolean isIgnored(Player player, String fromName) {
        boolean ignored = false;
        try {
            if (this.chatco != null) {
                ignored = this.chatco.getCCPlayer(player).isIgnored(fromName);
            }
        } catch (IOException e) {}
        
        return ignored;
    }
}