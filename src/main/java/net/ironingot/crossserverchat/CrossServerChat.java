package net.ironingot.crossserverchat;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class CrossServerChat extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");
    private ConfigHandler configHandler;
    private FirebaseHandler firebaseHandler;

    public void onEnable() {
        getCommand("crosserverchat").setExecutor(new CommandHandler(this));
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(this), this);
        configHandler = new ConfigHandler(this);
        firebaseHandler = new FirebaseHandler(this);

        CrossServerChat.logger.info(
            getDescription().getName() + "-" +
            getDescription().getVersion() + " is enabled!");
    }

    public void onDisable() {
        CrossServerChat.logger.info(getDescription().getName() + " is disabled");
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
