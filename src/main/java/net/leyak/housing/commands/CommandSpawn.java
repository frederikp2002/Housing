package net.leyak.housing.commands;

import net.leyak.housing.Housing;
import net.leyak.housing.handlers.PlayerInteraction;
import org.bukkit.entity.Player;

public class CommandSpawn {

    private final Housing plugin;
    private final PlayerInteraction playerInteraction;

    public CommandSpawn(Housing plugin) {
        this.plugin = plugin;
        this.playerInteraction = new PlayerInteraction(plugin);
    }

    public boolean teleportToHousingSpawn(String worldName, Player player) {
        if (plugin.getCore().getMVWorldManager().getMVWorld(worldName) != null) {
            playerInteraction.teleportPlayerToWorld(player, worldName);
            return true;
        }
        player.sendMessage(plugin.getMessageHandler().getMessage("command.housing.spawn.noHouse"));
        return false;
    }

    public boolean setHousingSpawn(String worldName, Player player) {
        if (plugin.getCore().getMVWorldManager().getMVWorld(worldName) != null) {
            playerInteraction.setSpawn(player, worldName);
            return true;
        }
        return true;
    }

}
