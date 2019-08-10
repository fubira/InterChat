package net.ironingot.nihongochat;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigHandler {
    private File configFile;
    private YamlConfiguration config;

    public ConfigHandler(File configFile) {
        this.configFile = configFile;
        this.config = YamlConfiguration.loadConfiguration(configFile);

        load();
    }

    public void load() {
        ConfigurationSection section = config.getConfigurationSection("NihongoChat");

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

    public Boolean getUserKanjiConversion(String name) {
        String path = "user." + name + ".kanji";
        Boolean usingKanjiConversion = (Boolean)config.get(path);

        if (usingKanjiConversion == null) {
            usingKanjiConversion = Boolean.TRUE;
            config.set(path, usingKanjiConversion);

            save();
        }

        return usingKanjiConversion;
    }

    public void setUserKanjiConversion(String name, Boolean value) {
        String path = "user." + name + ".kanji";
        config.set(path, value);

        save();
    }


    public Boolean getUserMode(String name) {
        String path = "user." + name + ".nihongochat";
        Boolean userMode = (Boolean)config.get(path);

        if (userMode == null) {
            userMode = Boolean.TRUE;
            config.set(path, userMode);

            save();
        }

        return userMode;
    }

    public void setUserMode(String name, Boolean value) {
        String path = "user." + name + ".nihongochat";
        config.set(path, value);

        save();
    }
}