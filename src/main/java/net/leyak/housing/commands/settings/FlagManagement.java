package net.leyak.housing.commands.settings;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.session.SessionManager;
import net.kyori.adventure.text.TextComponent;
import net.leyak.housing.Housing;
import net.leyak.housing.handlers.WorldManagement;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerEvent;

public class FlagManagement {
    WorldManagement worldManagement;

    public FlagManagement(Housing housing) {
        this.worldManagement = new WorldManagement(housing);
    }


    public void setCancelled(Cancellable e, boolean cancel, boolean notifyPlayer, TextComponent message) {
        e.setCancelled(cancel);
        Player player = null;

        if (e instanceof PlayerEvent) {
            PlayerEvent playerEvent = (PlayerEvent) e;
            player = playerEvent.getPlayer();
        } else if (e instanceof BlockBreakEvent) {
            BlockBreakEvent blockBreakEvent = (BlockBreakEvent) e;
            player = blockBreakEvent.getPlayer();
        } else if (e instanceof BlockPlaceEvent) {
            BlockPlaceEvent blockPlaceEvent = (BlockPlaceEvent) e;
            player = blockPlaceEvent.getPlayer();
        }

        if (e.isCancelled() && notifyPlayer && player != null) {
            player.sendMessage(message);
        }
    }

    public boolean hasBypass(Player player, com.sk89q.worldedit.world.World world) {
        LocalPlayer wrappedPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        return sessionManager.hasBypass(wrappedPlayer, world);
    }

    public void toggleRegionSettings(Player player, String worldName, StateFlag[] flags, RegionGroup group, String areaName, String enabledMsg, String disabledMsg, boolean useGlobalRegion) {
        ProtectedCuboidRegion region = worldManagement.getRegionsInPlayerHousing(worldName, areaName);
        GlobalProtectedRegion globalRegion = useGlobalRegion ? worldManagement.getGlobalRegionInPlayerHousing(worldName) : null;

        worldManagement.handleFlagSelection(flags, region, globalRegion, group);

        // Choose which message to send based on the first flag's state
        if (region.getFlag(flags[0]) == StateFlag.State.ALLOW) {
            player.sendMessage(enabledMsg);
        } else {
            player.sendMessage(disabledMsg);
        }
    }


}
