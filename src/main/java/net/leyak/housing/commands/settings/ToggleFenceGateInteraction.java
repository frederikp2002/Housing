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

public class ToggleFenceGateInteraction implements Listener {

    private final Housing plugin;
    WorldManagement worldManagement;
    FlagManagement flagManagement;
    Interactions interactions;

    public ToggleFenceGateInteraction(Housing plugin) {
        this.plugin = plugin;
        this.worldManagement = new WorldManagement(plugin);
        this.flagManagement = new FlagManagement(plugin);
        this.interactions = new Interactions(plugin);
    }

    public void toggleFenceGateInteraction(Player player, String worldName) {
        StateFlag[] flags = new StateFlag[]{Housing.FENCE_GATE_INTERACTION};
        flagManagement.toggleRegionSettings(
                player,
                worldName,
                flags,
                RegionGroup.ALL,
                "housing_area",
                "Fence Gate Interaction has been enabled",
                "Fence Gate Interaction has been disabled",
                false);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteraction(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            org.bukkit.Location location = Objects.requireNonNull(event.getClickedBlock()).getLocation();

            Material clickedBlockType = event.getClickedBlock().getType();
            if(isFenceGate(clickedBlockType) && interactions.cannotPlayerInteract(player, location, Housing.FENCE_GATE_INTERACTION)) {
                flagManagement.setCancelled(event, true, true, plugin.getMessageHandler().getMessage("command.housing.disabledInteractions"));
            }
        }
    }

    private boolean isFenceGate(Material type) {
        switch (type) {
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case MANGROVE_FENCE_GATE:
            case CRIMSON_FENCE_GATE:
            case WARPED_FENCE_GATE:
                return true;
            default:
                return false;
        }
    }

}
