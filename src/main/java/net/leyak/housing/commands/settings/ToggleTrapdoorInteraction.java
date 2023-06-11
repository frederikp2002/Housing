package net.leyak.housing.commands.settings;

import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.leyak.housing.Housing;
import net.leyak.housing.handlers.WorldManagement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class ToggleTrapdoorInteraction implements Listener {


    private final Housing plugin;
    WorldManagement worldManagement;
    FlagManagement flagManagement;
    Interactions interactions;

    public ToggleTrapdoorInteraction(Housing plugin) {
        this.plugin = plugin;
        this.worldManagement = new WorldManagement(plugin);
        this.flagManagement = new FlagManagement(plugin);
        this.interactions = new Interactions(plugin);
    }

    public void toggleTrapdoorInteraction(Player player, String worldName) {
        StateFlag[] flags = new StateFlag[]{Housing.TRAPDOOR_INTERACTION};
        flagManagement.toggleRegionSettings(
                player,
                worldName,
                flags,
                RegionGroup.ALL,
                "housing_area",
                "Trapdoor Interaction has been enabled",
                "Trapdoor Interaction has been disabled",
                false);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteraction(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            org.bukkit.Location location = Objects.requireNonNull(event.getClickedBlock()).getLocation();

            Material clickedBlockType = event.getClickedBlock().getType();
            if (isTrapdoor(clickedBlockType) && interactions.cannotPlayerInteract(player, location, Housing.TRAPDOOR_INTERACTION)){
                flagManagement.setCancelled(event, true, true, plugin.getMessageHandler().getMessage("command.housing.disabledInteractions"));
            }
        }
    }

    private boolean isTrapdoor(Material type) {
        switch (type) {
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case ACACIA_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case MANGROVE_TRAPDOOR:
            case CRIMSON_TRAPDOOR:
            case WARPED_TRAPDOOR:
                return true;
            default:
                return false;
        }
    }

}
