package net.ironingot.interchat;

import net.ironingot.interchat.InterChatPlugin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {
    private InterChatPlugin plugin;
    private File configFile;
    private YamlConfiguration config;

    public ConfigHandler(InterChatPlugin plugin) {
        this.plugin = plugin;

        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.plugin.saveDefaultConfig();
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        load();
    }

    public void load() {
        ConfigurationSection section = this.config.getConfigurationSection("InterChat");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                config.set(key, section.get(key));
            }
        } 
    }

    public void save() {
        try {
            config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getString(String path) {
        return (String)config.get(path);
    }

    protected Boolean getBoolean(String path) {
        return (Boolean)config.get(path);
    }

    public Boolean useTotalPlayerCount() {
        return getBoolean("server.useTotalPlayerCount");
    }

    public String getServerIdentify() {
        return getString("server.identify");
    }

    public String getServerColor() {
        return getString("server.color");
    }

    public String getRedisURI() {
        return getString("redis.uri");
    }
}
