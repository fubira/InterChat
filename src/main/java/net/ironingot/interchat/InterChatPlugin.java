package net.ironingot.interchat;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import net.ironingot.interchat.event.PlayerEventListener;
import net.ironingot.interchat.storage.RedisChatStorage;
import net.ironingot.interchat.interfaces.IChatStorage;

public class InterChatPlugin extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");
    private ConfigHandler configHandler;
    private RedisChatStorage chatStorage;
    private PlayerEventListener playerEventListener;

    public void onEnable() {
        this.configHandler = new ConfigHandler(this);
        this.chatStorage = new RedisChatStorage(this);
        this.playerEventListener = new PlayerEventListener(this);

        getCommand("interchat").setExecutor(new CommandHandler(this));
        getServer().getPluginManager().registerEvents(this.playerEventListener, this);

        chatStorage.open();
        playerEventListener.startReceive();

        InterChatPlugin.logger.info(
            getDescription().getName() + "-" +
            getDescription().getVersion() + " is enabled!");
    }

    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);

        this.playerEventListener.stopReceive();
        this.chatStorage.close();

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

    public IChatStorage getChatStorage() {
        return this.chatStorage; 
    }
}
