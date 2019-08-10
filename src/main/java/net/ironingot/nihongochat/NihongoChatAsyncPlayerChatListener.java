package net.ironingot.nihongochat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import biscotte.kana.Kana;

import net.ironingot.translator.KanaKanjiTranslator;

public class NihongoChatAsyncPlayerChatListener implements Listener {
    public NihongoChat plugin;

    private static final String excludeMatchString = "[^\u0020-\u007E]|\u00a7|u00a74u00a75u00a73u00a74v|^http|^[A-Z]";
    private static final Pattern excludePattern = Pattern.compile(excludeMatchString);


    private static final String prefixMatchString = "^([#GLOBAL#|>]+)(.*)";
    private static final Pattern prefixPattern = Pattern.compile(prefixMatchString);

    public NihongoChatAsyncPlayerChatListener(NihongoChat plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String prefix = "";
        String message = event.getMessage();
        Player player = event.getPlayer();

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

        Boolean toKana = plugin.getConfigHandler().getUserMode(player.getName());
        Boolean toKanji = plugin.getConfigHandler().getUserKanjiConversion(player.getName());
        String kanaMessage = ConvertMessage(message, toKana, toKanji);

        if (kanaMessage.equals(message)) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        String kanjiMessage = kanaMessage;

        if (!prefix.equals("")) {
            stringBuilder.append(prefix).append(" ");
        }
        stringBuilder.append(kanaMessage).append(" ").append(ChatColor.GRAY).append(message);

        event.setMessage(stringBuilder.toString());
    }

    public String ConvertMessage(String message, Boolean toKana, Boolean toKanji)
    {
        StringBuilder messageBuilder = new StringBuilder();

        if (!toKana.equals(Boolean.TRUE))
            return message;

        for (String word: message.split(" "))
        {
            if (messageBuilder.length() > 0)
            {
                messageBuilder.append(" ");
            }

            Matcher excludeMatcher = excludePattern.matcher(word);
            if (excludeMatcher.find())
            {
                messageBuilder.append(word);
            } else {
                Kana kana = new Kana();
                kana.setLine(word);
                kana.convert();
                String kanaWord = kana.getLine();

                if (toKanji.equals(Boolean.TRUE) && !kanaWord.matches("^[A-Za-z].*"))
                {
                    kanaWord = KanaKanjiTranslator.translate(kanaWord);
                }

                messageBuilder.append(kanaWord);
            }
        }

        return messageBuilder.toString();
    }
}
