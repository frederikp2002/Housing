package net.leyak.housing.handlers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class ConfigHandler {

    private final JavaPlugin plugin;
    private YamlConfiguration config;

    public ConfigHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        try {
            File configFile = new File(plugin.getDataFolder(), "config.yml");

            if (!configFile.exists()) {
                plugin.saveResource("config.yml", false);
                plugin.getLogger().info("config.yml has been created!");
            }

            config = YamlConfiguration.loadConfiguration(configFile);

        } catch(Exception e) {
            plugin.getLogger().severe("Error loading config.yml!" + e.getMessage());
        }
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public boolean getBool(String key) {
        return config.getBoolean(key);
    }

    public List<String> getStringList(String key) {
        return config.getStringList(key);
    }

    public void reloadConfig() {
        loadConfig();
    }

}
