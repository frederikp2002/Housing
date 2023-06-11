package net.leyak.housing;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import net.leyak.housing.commands.CommandHouse;
import net.leyak.housing.commands.settings.*;
import net.leyak.housing.handlers.ConfigHandler;
import net.leyak.housing.handlers.MessageHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Housing extends JavaPlugin {

    private MessageHandler messageHandler;
    private ConfigHandler configHandler;
    private MultiverseCore core;
    public static StateFlag DOOR_INTERACTION;
    public static StateFlag TRAPDOOR_INTERACTION;
    public static StateFlag FENCE_GATE_INTERACTION;
    public static StateFlag HOUSING_BUILD_ACCESS;
    public static StateFlag HOUSING_CHEST_ACCESS;

    @Override
    public void onEnable() {
        core = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        this.messageHandler = new MessageHandler(this);
        this.configHandler = new ConfigHandler(this);
        // Plugin startup logic
        getLogger().info(messageHandler.getMessage("plugin.dependencies.multiverseCore.found").content());
        getLogger().info(messageHandler.getMessage("plugin.dependencies.worldGuard.found").content());
        Objects.requireNonNull(this.getCommand("housing")).setExecutor(new CommandHouse(this));
        getServer().getPluginManager().registerEvents(new Settings(this), this);
        getServer().getPluginManager().registerEvents(new ToggleChestAccess(this), this);
        getServer().getPluginManager().registerEvents(new ToggleDoorInteraction(this), this);
        getServer().getPluginManager().registerEvents(new ToggleFenceGateInteraction(this), this);
        getServer().getPluginManager().registerEvents(new ToggleTrapdoorInteraction(this), this);
        getServer().getPluginManager().registerEvents(new ToggleBuildAccess(this), this);
        getLogger().info(messageHandler.getMessage("plugin.housing.enabled").content());
    }

    @Override
    public void onDisable() {
        getLogger().info(messageHandler.getMessage("plugin.housing.disabled").content());
    }

    public MessageHandler getMessageHandler() { return messageHandler; }
    public ConfigHandler getConfigHandler() { return configHandler; }
    public MultiverseCore getCore() { return core; }

    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        registry.register(DOOR_INTERACTION = new StateFlag("door-interaction", true));
        registry.register(TRAPDOOR_INTERACTION = new StateFlag("trapdoor-interaction", true));
        registry.register(FENCE_GATE_INTERACTION = new StateFlag("fence-gate-interaction", true));
        registry.register(HOUSING_BUILD_ACCESS = new StateFlag("housing-build-access", true));
        registry.register(HOUSING_CHEST_ACCESS = new StateFlag("housing-chest-access", true));
    }

}
