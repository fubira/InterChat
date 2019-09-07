package net.ironingot.interchat;

import net.ironingot.interchat.event.PlayerChatEventListener;
import net.ironingot.interchat.event.PlayerDeathEventListener;
import net.ironingot.interchat.event.PlayerJoinLeaveEventListener;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class InterChatPlugin extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");
    private ConfigHandler configHandler;
    private InterChatMessenger messenger;
    
    public InterChatMessenger getMessenger() {
        return this.messenger;
    }

    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    public void onEnable() {
        this.load();
        this.registerEvents();
        InterChatPlugin.logger.info(getDescription().getName() + "-" + getDescription().getVersion() + " is enabled");
    }

    public void onDisable() {
        this.unload();
        InterChatPlugin.logger.info(getDescription().getName() + "-" + getDescription().getVersion() + " is disabled");
    }

    public void load() {
        this.configHandler = new ConfigHandler(this);
        this.getCommand("interchat").setExecutor(new CommandHandler(this));
        InterChatProtocol.enable(this);

        this.messenger = new InterChatMessenger(this);
        this.messenger.enable();
    }

    public void unload() {
        InterChatProtocol.disable(this);
        this.messenger.disable();
    }

    public void reload() {
        this.unload();
        this.load();
    }

    protected void registerEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager(); 
        pluginManager.registerEvents(new PlayerChatEventListener(this, this.messenger), this);
        pluginManager.registerEvents(new PlayerDeathEventListener(this, this.messenger), this);
        pluginManager.registerEvents(new PlayerJoinLeaveEventListener(this, this.messenger), this);
    }

}
