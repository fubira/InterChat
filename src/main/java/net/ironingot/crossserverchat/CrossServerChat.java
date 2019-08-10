package net.ironingot.crossserverchat;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class CrossServerChat extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");
    private ConfigHandler configHandler;

    public void onEnable() {
        new AsyncPlayerChatListener(this);

        loadConfig();
        getCommand("nihongochat").setExecutor(new CommandHandler(this));

        logger.info(getDescription().getName() + "-" +
                    getDescription().getVersion() + " is enabled!");
    }

    public void onDisable() {
        logger.info(getDescription().getName() + " is disabled");
    }

    public void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");

        try {
            configFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        configHandler = new ConfigHandler(configFile);
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
