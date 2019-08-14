package net.ironingot.interchat;

import org.bukkit.plugin.java.JavaPlugin;

public class InterChatPlugin extends JavaPlugin {
    private ConfigHandler configHandler;
    private InterChat interChat;
    
    public InterChat getInstance() {
        return this.interChat;
    }

    public void onEnable() {
        this.configHandler = new ConfigHandler(this);
        getCommand("interchat").setExecutor(new CommandHandler(this));
        this.interChat = new InterChat(this);
        this.interChat.enable();

        InterChat.logger.info(
            getDescription().getName() + "-" +
            getDescription().getVersion() + " is enabled!");
    }

    public void onDisable() {
        this.interChat.disable();

        InterChat.logger.info(
            getDescription().getName() + "-" +
            getDescription().getVersion() + " is disabled");
    }

    public void reload() {
        this.configHandler = new ConfigHandler(this);
    }

    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }
}
