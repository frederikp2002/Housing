package net.leyak.housing.commands;

import net.kyori.adventure.text.Component;
import net.leyak.housing.Housing;
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
    private final CommandReload reload;
    private final CommandHelp commandHelp;

    public CommandHouse(Housing plugin) {
        this.plugin = plugin;
        this.worldManagement = new WorldManagement(plugin);
        this.playerInteraction = new PlayerInteraction(plugin);
        this.settings = new Settings(plugin);
        this.reload = new CommandReload(plugin);
        this.commandHelp = new CommandHelp(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            plugin.getLogger().warning(plugin.getMessageHandler().getMessage("command.housing.notPlayer").content());
            return false;
        }

        if (!command.getName().equalsIgnoreCase("housing")) return false;

        Player player = (Player) commandSender;
        String worldName = "housing-" + player.getUniqueId() + "-1";

        List<String> settingsCommands = plugin.getConfigHandler().getStringList("commands.housing.settings.argument");

        // "/housing" command without any arguments
        if (strings.length == 0) {
            if (plugin.getCore().getMVWorldManager().getMVWorld(worldName) != null) {
                settings.openSettingsGUI(player);
                return true;
            }
            worldManagement.createNewHousing(player, worldName);
            return true;
        }
        // "/housing <argument>" command
        else {
            // "/housing settings" command
            if (settingsCommands.contains(strings[0])) {
                settings.openSettingsGUI(player);
                return true;
            // "/housing reload" command
            } else if (strings[0].equalsIgnoreCase("reload")) {
                reload.reload(player);
                return true;
            // "/housing spawn" command
            } else if (strings[0].equalsIgnoreCase("spawn")) {
                if (plugin.getCore().getMVWorldManager().getMVWorld(worldName) != null) {
                    playerInteraction.teleportPlayerToWorld(player, worldName);
                    return true;
                }
                player.sendMessage(plugin.getMessageHandler().getMessage("command.housing.spawn.noHouse"));
                return true;
            // "/housing help" command
            } else if (strings[0].equalsIgnoreCase("commandHelp")) {
                List<Component> helpMessages = commandHelp.help();
                for (Component message : helpMessages) {
                    player.sendMessage(message);
                }
                return true;
            }
            // "/housing <invalid_argument>" command
            else {
                player.sendMessage(plugin.getMessageHandler().getMessage("command.housing.invalidArgument", s, s));
                return true;
            }
        }
    }


}
