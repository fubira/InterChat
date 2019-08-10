package net.ironingot.crossserverchat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import biscotte.kana.Kana;

import net.ironingot.translator.KanaKanjiTranslator;

public class AsyncPlayerChatListener implements Listener {
    public CrossServerChat plugin;

    private static final String excludeMatchString = "[^\u0020-\u007E]|\u00a7|u00a74u00a75u00a73u00a74v|^http|^[A-Z]";
    private static final Pattern excludePattern = Pattern.compile(excludeMatchString);


    private static final String prefixMatchString = "^([#GLOBAL#|>]+)(.*)";
    private static final Pattern prefixPattern = Pattern.compile(prefixMatchString);

    public AsyncPlayerChatListener(CrossServerChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String prefix = "";
        String message = event.getMessage();
        Player player = event.getPlayer();
        UUID uid = player.getUniqueId();
        CrossServerChat.logger.info("server name:" + this.plugin.getServer().getServerName());
        CrossServerChat.logger.info("server id:" + this.plugin.getServer().getServerId());

        CrossServerChat.logger.info("message: " + message + " player: " + player + " fmt:" + event.getFormat() + " uid:" + uid);
        if (message.startsWith("/")) {
            return;
        }

        if (!message.matches("^[A-Za-z].*")) {
            return;
        }

        Matcher prefixMatcher = prefixPattern.matcher(message);
        if (prefixMatcher.find(0)) {
            prefix = prefixMatcher.group(1);
            message = prefixMatcher.group(2);
        }

        // event.setMessage(stringBuilder.toString());
    }
}
