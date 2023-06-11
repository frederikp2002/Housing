package net.leyak.housing.handlers;

import net.leyak.housing.Housing;
import org.bukkit.entity.Player;

public class PlayerInteraction {
    private final Housing plugin;

    public PlayerInteraction(Housing plugin) {
        this.plugin = plugin;
    }

    public void teleportPlayerToWorld(Player player, String worldName) {
        player.teleport(plugin.getCore().getMVWorldManager().getMVWorld(worldName).getSpawnLocation());
        player.sendMessage(plugin.getMessageHandler().getMessage("command.housing.teleported"));
    }

    public void setSpawn(Player player, String worldName) {
        plugin.getCore().getMVWorldManager().getMVWorld(worldName).setSpawnLocation(player.getLocation());
        player.sendMessage(plugin.getMessageHandler().getMessage("command.housing.setSpawn"));
    }

}
