package net.ironingot.crossserverchat;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitRunnable;

public class CrossServerChat extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");
    private ConfigHandler configHandler;
    private RedisChatStorage chatStorage;
    private PlayerEventListener playerEventListener;

    public void onEnable() {
        this.configHandler = new ConfigHandler(this);
        this.chatStorage = new RedisChatStorage(this);
        this.playerEventListener = new PlayerEventListener(this);

        getCommand("crossserverchat").setExecutor(new CommandHandler(this));
        getServer().getPluginManager().registerEvents(this.playerEventListener, this);

        chatStorage.open();
        playerEventListener.startReceive();

        CrossServerChat.logger.info(
            getDescription().getName() + "-" +
            getDescription().getVersion() + " is enabled!");
    }

    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);

        this.playerEventListener.stopReceive();
        this.chatStorage.close();

        CrossServerChat.logger.info(
            getDescription().getName() + "-" +
            getDescription().getVersion() + " is disabled");
    }

    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    public IChatStorage getChatStorage() {
        return this.chatStorage; 
    }
}
