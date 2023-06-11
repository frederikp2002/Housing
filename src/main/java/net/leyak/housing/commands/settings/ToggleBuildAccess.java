package net.leyak.housing.commands.settings;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.leyak.housing.Housing;
import net.leyak.housing.handlers.WorldManagement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class ToggleBuildAccess implements Listener {

    private final Housing plugin;
    WorldManagement worldManagement;
    FlagManagement flagManagement;

    public ToggleBuildAccess(Housing plugin) {
        this.plugin = plugin;
        this.worldManagement = new WorldManagement(plugin);
        this.flagManagement = new FlagManagement(plugin);
    }

    public void toggleBuildAccess(Player player, String worldName) {
        StateFlag[] flags = new StateFlag[]{Housing.HOUSING_BUILD_ACCESS};
        flagManagement.toggleRegionSettings(player, worldName, flags, RegionGroup.MEMBERS, "housing_area",
                "Build access for residents has been enabled",
                "Build access for residents has been disabled",
                false);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBuildPlacing(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!(canPlayerBuild(player, event.getBlock().getLocation(), Housing.HOUSING_BUILD_ACCESS))) {
            flagManagement.setCancelled(event, true, true, plugin.getMessageHandler().getMessage("command.housing.disabledBlockPlacement"));
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBuildBreaking(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!(canPlayerBuild(player, event.getBlock().getLocation(), Housing.HOUSING_BUILD_ACCESS))) {
            flagManagement.setCancelled(event, true, true, plugin.getMessageHandler().getMessage("command.housing.disabledBlockBreaking"));
        }
    }

    private boolean canPlayerBuild(Player player, org.bukkit.Location location, StateFlag flag) {
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location wrappedLocation = BukkitAdapter.adapt(location);
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        if (flagManagement.hasBypass(player, world)) {
            return true;
        }

        ApplicableRegionSet regions = query.getApplicableRegions(wrappedLocation);

        for (ProtectedRegion region : regions) {
            // If the player is an owner of the region or has bypass permissions, they can always interact.
            if (region.isOwner(localPlayer)) {
                return true;
            }

            // Otherwise, check the flag for members.
            StateFlag.State flagState = region.getFlag(flag);
            if (flagState == StateFlag.State.DENY && region.isMember(localPlayer)) {
                return false;
            }
        }

        // By default, allow the interaction.
        return true;
    }

}
