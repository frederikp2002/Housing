package net.leyak.housing.commands;

import net.kyori.adventure.text.Component;
import net.leyak.housing.Housing;

import java.util.List;

public class CommandHelp {
    private final Housing plugin;

    public CommandHelp(Housing plugin) {
        this.plugin = plugin;
    }

    public List<Component> help() {
        List<Component> settingsCommands = plugin.getMessageHandler().getMessageListFormatted("command.housing.help.commands");

        if (settingsCommands.isEmpty()) {
            settingsCommands.add(Component.text("This list is empty. Please inform a server administrator."));
        }

        return settingsCommands;
    }

}
