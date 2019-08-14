package net.ironingot.interchat;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class InterChatPlugin extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");
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

        InterChatPlugin.logger.info(
            getDescription().getName() + "-" +
            getDescription().getVersion() + " is enabled!");
    }

    public void onDisable() {
        this.interChat.disable();

        InterChatPlugin.logger.info(
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
