package net.ironingot.crossserverchat;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class CrossServerChat extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");
    private ConfigHandler configHandler;

    void initialize() {
        configHandler = new ConfigHandler(getDataFolder());
        new AsyncPlayerChatListener(this);
        getCommand("nihongochat").setExecutor(new CommandHandler(this));
    }

    public void onEnable() {
        initialize();

        logger.info(
            getDescription().getName() + "-" +
            getDescription().getVersion() + " is enabled!");
    }

    public void onDisable() {
        logger.info(getDescription().getName() + " is disabled");
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
