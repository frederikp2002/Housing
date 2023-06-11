package net.leyak.housing.commands;

import net.leyak.housing.Housing;
import org.bukkit.entity.Player;

public class CommandReload {
    private final Housing plugin;

    public CommandReload(Housing plugin) {
        this.plugin = plugin;
    }

    public void reload(Player player) {
        if (plugin.getConfigHandler().getString("housing.permissions.reload")  == null) {
            plugin.getLogger().severe("The permission node for the reload command is missing from the config.yml file. Please add it and reload the plugin."); return; }

        if (player.hasPermission(plugin.getConfigHandler().getString("housing.permissions.reload"))) {
            plugin.reloadConfig();
            plugin.getMessageHandler().reloadMessages();
            plugin.getConfigHandler().reloadConfig();
            player.sendMessage(plugin.getMessageHandler().getMessage("housing.commands.reload"));
        } else {
            player.sendMessage(plugin.getMessageHandler().getMessage("core.commands.noPermission"));
        }
    }
}
