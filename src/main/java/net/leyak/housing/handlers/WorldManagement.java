package net.leyak.housing.handlers;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.leyak.housing.Housing;
import net.leyak.housing.housingmanagement.CreateHousing;
import net.leyak.housing.housingmanagement.ModifyHousing;
import org.bukkit.entity.Player;

import java.util.Objects;

public class WorldManagement {
    private final Housing plugin;
    private final CreateHousing createHousing;
    private final ModifyHousing modifyHousing;

    public WorldManagement(Housing plugin) {
        this.plugin = plugin;
        this.createHousing = new CreateHousing(plugin);
        this.modifyHousing = new ModifyHousing(plugin);
    }

    private void createHousing(String name, Player player) {
        createHousing.createHousing(name);
        modifyHousing.modifyHousing(name);
        player.sendMessage(plugin.getMessageHandler().getMessage("command.housing.created"));
        player.teleport(plugin.getCore().getMVWorldManager().getMVWorld(name).getSpawnLocation());
    }

    private void createHouseRegion(World world, Player player) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(world);
        BlockVector3 min = BlockVector3.at(-10, 319, 10);
        BlockVector3 max = BlockVector3.at(10, -59, -10);
        StateFlag[] flags = new StateFlag[]{
                Flags.CHEST_ACCESS
        };

        ProtectedCuboidRegion region = new ProtectedCuboidRegion("housing_area", min, max);
        region.setFlag(Flags.TNT, StateFlag.State.DENY);
        region.setFlag(Flags.PVP, StateFlag.State.DENY);
        region.setFlag(Flags.FALL_DAMAGE, StateFlag.State.DENY);
        region.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
        region.setFlag(Flags.DAMAGE_ANIMALS, StateFlag.State.DENY);
        region.setFlag(Flags.SLEEP, StateFlag.State.DENY);
        region.setFlag(Flags.PLACE_VEHICLE, StateFlag.State.DENY);
        region.setFlag(Flags.POTION_SPLASH, StateFlag.State.DENY);
        region.setFlag(Flags.TRAMPLE_BLOCKS, StateFlag.State.DENY);
        region.setFlag(Flags.ENDERPEARL, StateFlag.State.DENY);
        region.setFlag(Flags.PISTONS, StateFlag.State.DENY);
        region.setFlag(Housing.HOUSING_CHEST_ACCESS, StateFlag.State.DENY);
        region.setFlag(Housing.DOOR_INTERACTION, StateFlag.State.ALLOW);
        region.setFlag(Housing.TRAPDOOR_INTERACTION, StateFlag.State.ALLOW);
        region.setFlag(Housing.FENCE_GATE_INTERACTION, StateFlag.State.ALLOW);
        region.setFlag(Housing.HOUSING_BUILD_ACCESS, StateFlag.State.DENY);
        flagSelection(flags, region, null, RegionGroup.MEMBERS);
        region.getOwners().addPlayer(player.getUniqueId());
        region.setPriority(100);
        Objects.requireNonNull(regions).addRegion(region);
    }

    private void setupGlobalRegion(World world) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(world);
        GlobalProtectedRegion globalRegion = new GlobalProtectedRegion("__global__");
        globalRegion.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.PVP, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.FALL_DAMAGE, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.DAMAGE_ANIMALS, StateFlag.State.DENY);
        globalRegion.setFlag(Flags.CHEST_ACCESS, StateFlag.State.DENY);
        globalRegion.setFlag(Housing.DOOR_INTERACTION, StateFlag.State.ALLOW);
        globalRegion.setFlag(Housing.TRAPDOOR_INTERACTION, StateFlag.State.ALLOW);
        globalRegion.setFlag(Housing.FENCE_GATE_INTERACTION, StateFlag.State.ALLOW);
        if (regions != null) {
            regions.addRegion(globalRegion);
        }
    }

    public ProtectedCuboidRegion getRegionsInPlayerHousing(String worldName, String regionName) {
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(worldName);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(world);

        if (regions == null) {
            // handle the error, maybe log it or throw an exception, depending on your application's needs
            return null;
        }

        return (ProtectedCuboidRegion) regions.getRegion(regionName);
    }

    public GlobalProtectedRegion getGlobalRegionInPlayerHousing(String worldName) {
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(worldName);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(world);

        if (regions == null) {
            // handle the error, maybe log it or throw an exception, depending on your application's needs
            return null;
        }

        return (GlobalProtectedRegion) regions.getRegion("__global__");
    }

    private void flagSelection(StateFlag[] flags, ProtectedCuboidRegion region, GlobalProtectedRegion globalRegion, RegionGroup targetGroup) {
        for (StateFlag flag : flags) {
            StateFlag.State currentState = region.getFlag(flag);
            RegionGroupFlag groupFlag = flag.getRegionGroupFlag();

            StateFlag.State newState = (currentState == StateFlag.State.ALLOW ? StateFlag.State.DENY : StateFlag.State.ALLOW);

            region.setFlag(flag, newState);
            region.setFlag(groupFlag, targetGroup);

            if (globalRegion != null) {
                globalRegion.setFlag(flag, newState);
                globalRegion.setFlag(groupFlag, targetGroup);
            }

        }
    }

    public void handleFlagSelection(StateFlag[] flags, ProtectedCuboidRegion region, GlobalProtectedRegion globalRegion, RegionGroup targetGroup) {
        flagSelection(flags, region, globalRegion, targetGroup);
    }

    public void createNewHousing(Player player, String worldName) {
        createHousing(worldName, player);
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(worldName);
        createHouseRegion(world, player);
        setupGlobalRegion(world);
    }

}
