package net.ironingot.crossserverchat;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigHandler {
    private CrossServerChat plugin;
    private File configFile;
    private YamlConfiguration config;

    public ConfigHandler(CrossServerChat plugin) {
        this.plugin = plugin;

        this.configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            this.plugin.saveDefaultConfig();
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);

        load();
    }

    public void load() {
        ConfigurationSection section = config.getConfigurationSection("CrossServerChat");

        if (section != null) {
            for (String key : section.getKeys(false)) {

                config.set(key, section.get(key));
            }
        } 

        save();
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerIdentify() {
        String path = "server.identify";
        return (String)config.get(path);
    }

    public String getFirestoreURL() {
        String path = "firestore.url";
        return (String)config.get(path);
    }

    public String getFirestoreAuthKey() {
        String path = "firestore.auth-key";
        return (String)config.get(path);
    }

    public String getRedisURI() {
        String path = "redis.uri";
        return (String)config.get(path);
    }
}
