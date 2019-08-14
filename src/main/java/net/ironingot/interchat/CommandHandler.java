package net.ironingot.interchat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {
    private InterChatPlugin plugin;
    private String pluginName;
    private String pluginVersion;

    public CommandHandler(InterChatPlugin plugin){
        this.plugin = plugin;
        this.pluginName = plugin.getDescription().getName();
        this.pluginVersion = plugin.getDescription().getVersion();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String command;
        String option;

        command = (args.length >= 1) ? args[0].toLowerCase() : "version";
        option  = (args.length >= 2) ? args[1].toLowerCase() : null;

        return executeCommand(sender, command, option);
    }

    private boolean executeCommand(CommandSender sender, String command, String option) {
        if (command != null && command.equals("version")) {
            sender.sendMessage(ChatColor.GOLD + this.pluginName + "-" + this.pluginVersion);
            return true;
        }
        if (command.equals("reload")) {
            this.plugin.reload();
            sender.sendMessage(ChatColor.GOLD + this.pluginName + ": configuration reloaded.");
            return true;
        }
        return true;
    }
}