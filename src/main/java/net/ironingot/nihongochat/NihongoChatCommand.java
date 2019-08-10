package net.ironingot.nihongochat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NihongoChatCommand implements CommandExecutor {
    private NihongoChat plugin;
    private String pluginName;
    private String pluginVersion;

    public NihongoChatCommand(NihongoChat plugin){
        this.plugin = plugin;
        this.pluginName = plugin.getDescription().getName();
        this.pluginVersion = plugin.getDescription().getVersion();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String command;
        String option;

        command = (args.length >= 1) ? args[0].toLowerCase() : "get";
        option  = (args.length >= 2) ? args[1].toLowerCase() : null;

        return executeCommand(sender, command, option);
    }

    private boolean executeCommand(CommandSender sender, String command, String option) {
        if (command != null && command.equals("version")) {
            sender.sendMessage(ChatColor.GOLD + this.pluginName + "-" + this.pluginVersion);
            return true;
        }

        if (command != null && command.equals("kanji")) {
            if (option != null && (option.equals("on") || option.equals("true"))) {
                plugin.getConfigHandler().setUserKanjiConversion(sender.getName(), Boolean.TRUE);
            }
            if (option != null && (option.equals("off") || option.equals("false"))) {
                plugin.getConfigHandler().setUserKanjiConversion(sender.getName(), Boolean.FALSE);
            }

            if (plugin.getConfigHandler().getUserKanjiConversion(sender.getName())) {
                sender.sendMessage(ChatColor.GOLD + pluginName + " Kanji conversion is enabled.");
            } else {
                sender.sendMessage(ChatColor.GOLD + pluginName + " Kanji conversion is disabled.");
            }
            return true;
        }

        if (command != null) {
            if (command.equals("on") || command.equals("true")) {
                plugin.getConfigHandler().setUserMode(sender.getName(), Boolean.TRUE);
            }
            if (command.equals("off") || command.equals("false")) {
                plugin.getConfigHandler().setUserMode(sender.getName(), Boolean.FALSE);
            }
        }

        if (plugin.getConfigHandler().getUserMode(sender.getName()) == Boolean.TRUE) {
            sender.sendMessage(ChatColor.GOLD + pluginName + " is enabled.");
        } else {
            sender.sendMessage(ChatColor.GOLD + pluginName + " is disabled.");
        }
        return true;
    }
}