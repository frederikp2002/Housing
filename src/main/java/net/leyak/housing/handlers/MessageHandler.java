package net.leyak.housing.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageHandler {

    private final JavaPlugin plugin;

    private YamlConfiguration messagesConfig;

    public MessageHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
            plugin.getLogger().info("messages.yml has been created!");
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public TextComponent getMessage(String key, Object... args) {
        if (messagesConfig == null) { return Component.text("Messages not found!"); }

        String message = messagesConfig.getString(key);

        if (message != null) {
            message = String.format(message, args);
            return (TextComponent) MiniMessage.miniMessage().deserialize(message);
        } else {
            return Component.text("Message not found: " + key);
        }
    }

    public List<String> getMessageList(String key) {
        if (messagesConfig == null) {
            return Collections.singletonList("Messages not found!");
        }

        List<String> messageList = messagesConfig.getStringList(key);

        if (!messageList.isEmpty()) {
            return messageList;
        } else {
            return Collections.singletonList("Message not found: " + key);
        }
    }

    public List<Component> getMessageListFormatted(String key) {
        if (messagesConfig == null) {
            return Collections.singletonList(MiniMessage.miniMessage().deserialize("Messages not found!"));
        }

        List<String> messageList = messagesConfig.getStringList(key);

        if (!messageList.isEmpty()) {
            // Create a new list for the formatted messages
            List<Component> formattedMessages = new ArrayList<>();

            // Iterate over each message in the list
            for (String message : messageList) {
                // Format the message using MiniMessage and add it to the formatted messages list
                formattedMessages.add(MiniMessage.miniMessage().deserialize(message));
            }

            // Return the list of formatted messages
            return formattedMessages;
        } else {
            return Collections.singletonList(MiniMessage.miniMessage().deserialize("Message not found: " + key));
        }
    }

    public void reloadMessages() {
        loadMessages();
    }


}
