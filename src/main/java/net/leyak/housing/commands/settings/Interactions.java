package net.leyak.housing.commands.settings;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.leyak.housing.Housing;
import net.leyak.housing.handlers.WorldManagement;
import org.bukkit.entity.Player;

public class Interactions {

    WorldManagement worldManagement;
    FlagManagement flagManagement;

    public Interactions(Housing plugin) {
        this.worldManagement = new WorldManagement(plugin);
        this.flagManagement = new FlagManagement(plugin);
    }

    public boolean cannotPlayerInteract(Player player, org.bukkit.Location location, StateFlag flag) {
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location wrappedLocation = BukkitAdapter.adapt(location);
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        if (flagManagement.hasBypass(player, world)) {
            return false;
        }

        ApplicableRegionSet regions = query.getApplicableRegions(wrappedLocation);

        // If the flag is allowed, or if the player is a member of the region, then return false.
        if (regions.testState(localPlayer, flag)) {
            return false;
        }

        for (ProtectedRegion region : regions) {
            if (region.isMember(localPlayer)) {
                return false;
            }
        }

        // Return true if the flag test fails and the player is not a member of the region.
        return true;
    }

}
