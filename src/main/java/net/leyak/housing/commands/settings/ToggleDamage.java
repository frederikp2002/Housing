package net.leyak.housing.commands.settings;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.leyak.housing.Housing;
import net.leyak.housing.handlers.WorldManagement;
import org.bukkit.entity.Player;

public class ToggleDamage {
    WorldManagement worldManagement;
    FlagManagement flagManagement;

    public ToggleDamage(Housing plugin) {
        this.worldManagement = new WorldManagement(plugin);
        this.flagManagement = new FlagManagement(plugin);
    }

    public void toggleDamage(Player player, String worldName) {
        StateFlag[] flags = new StateFlag[]{Flags.PVP, Flags.FALL_DAMAGE, Flags.MOB_DAMAGE, Flags.DAMAGE_ANIMALS};
        flagManagement.toggleRegionSettings(player, worldName, flags, RegionGroup.ALL, "housing_area",
                "Damage has been enabled",
                "Damage has been disabled",
                true);
    }

}
