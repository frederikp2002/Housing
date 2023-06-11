package net.leyak.housing.commands;

import net.kyori.adventure.text.Component;
import net.leyak.housing.Housing;
import net.leyak.housing.commands.resident.ResidentGui;
import net.leyak.housing.commands.settings.Settings;
import net.leyak.housing.handlers.PlayerInteraction;
import net.leyak.housing.handlers.WorldManagement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandHouse implements CommandExecutor {

    private final Housing plugin;
    private final WorldManagement worldManagement;
    private final PlayerInteraction playerInteraction;
    private final Settings settings;
    private final ResidentGui resident;
    private final CommandHelp commandHelp;
    private final CommandReload reload;

    private static final String RELOAD_COMMAND = "reload";
    private static final String SPAWN_COMMAND = "spawn";
    private static final String HELP_COMMAND = "help";

    public CommandHouse(Housing plugin) {
        this.plugin = plugin;
        this.worldManagement = new WorldManagement(plugin);
        this.playerInteraction = new PlayerInteraction(plugin);
        this.settings = new Settings(plugin);
        this.resident = new ResidentGui(plugin);
        this.commandHelp = new CommandHelp(plugin);
        this.reload = new CommandReload(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) { sendNotPlayerWarning(); return false; }
        if (!command.getName().equalsIgnoreCase("housing")) return false;

        Player player = (Player) commandSender;
        String worldName = "housing-" + player.getUniqueId() + "-1";

        if (strings.length == 0) {
            return handleNoArgumentsCommand(player, worldName);
        } else {
            return handleCommandWithArguments(player, worldName, strings[0]);
        }
    }

    private void sendNotPlayerWarning() {
        plugin.getLogger().warning(plugin.getMessageHandler().getMessage("command.housing.notPlayer").content());
    }

    private boolean handleNoArgumentsCommand(Player player, String worldName) {
        if (plugin.getCore().getMVWorldManager().getMVWorld(worldName) != null) {
            settings.openSettingsGUI(player);
            return true;
        }
        worldManagement.createNewHousing(player, worldName);
        return true;
    }

    private boolean handleCommandWithArguments(Player player, String worldName, String firstArg) {
        List<String> settingsCommands = plugin.getConfigHandler().getStringList("commands.housing.settings.argument");
        List<String> residentCommands = plugin.getConfigHandler().getStringList("commands.housing.resident.argument");

        if (settingsCommands.contains(firstArg.toLowerCase())) {
            settings.openSettingsGUI(player);
            return true;
        } else if (residentCommands.contains(firstArg.toLowerCase())) {
            resident.openResidentGUI(player);
            return true;
        } else if (firstArg.equalsIgnoreCase(RELOAD_COMMAND)) {
            reload.reload(player);
            return true;
        } else if (firstArg.equalsIgnoreCase(SPAWN_COMMAND)) {
            return handleSpawnCommand(player, worldName);
        } else if (firstArg.equalsIgnoreCase(HELP_COMMAND)) {
            return handleHelpCommand(player);
        } else {
            player.sendMessage(plugin.getMessageHandler().getMessage("command.housing.invalidArgument", firstArg, firstArg));
            return true;
        }
    }

    private boolean handleSpawnCommand(Player player, String worldName) {
        if (plugin.getCore().getMVWorldManager().getMVWorld(worldName) != null) {
            playerInteraction.teleportPlayerToWorld(player, worldName);
            return true;
        }
        player.sendMessage(plugin.getMessageHandler().getMessage("command.housing.spawn.noHouse"));
        return false;
    }

    private boolean handleHelpCommand(Player player) {
        List<Component> helpMessages = commandHelp.help();
        for (Component message : helpMessages) {
            player.sendMessage(message);
        }
        return true;
    }
}
