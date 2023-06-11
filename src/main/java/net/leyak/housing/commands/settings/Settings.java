package net.leyak.housing.commands.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.leyak.housing.Housing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Settings implements Listener {

    private final Housing plugin;
    private final List<SettingsItem> settingsItems = new ArrayList<>();
    private final TextComponent title = Component.text("Housing Settings");
    ToggleBuildAccess toggleBuildAccess;
    ToggleChestAccess toggleChestAccess;
    ToggleDamage toggleDamage;
    ToggleDoorInteraction toggleDoorInteraction;
    ToggleFenceGateInteraction toggleFenceGateInteraction;
    ToggleTrapdoorInteraction toggleTrapdoorInteraction;

    public Settings(Housing plugin) {
        this.plugin = plugin;
        this.toggleBuildAccess = new ToggleBuildAccess(plugin);
        this.toggleChestAccess = new ToggleChestAccess(plugin);
        this.toggleDamage = new ToggleDamage(plugin);
        this.toggleDoorInteraction = new ToggleDoorInteraction(plugin);
        this.toggleFenceGateInteraction = new ToggleFenceGateInteraction(plugin);
        this.toggleTrapdoorInteraction = new ToggleTrapdoorInteraction(plugin);

        settingsItems.add(new SettingsItem(
                Material.FEATHER,
                plugin.getMessageHandler().getMessage("command.housing.settings.item.toggleFlight.name"),
                getLore("command.housing.settings.item.toggleFlight.lore"),
                11));
        settingsItems.add(new SettingsItem(
                Material.NETHER_STAR,
                plugin.getMessageHandler().getMessage("command.housing.settings.item.toggleMagic.name"),
                getLore("command.housing.settings.item.toggleMagic.lore"),
                13));
        settingsItems.add(new SettingsItem(
                Material.IRON_SWORD,
                plugin.getMessageHandler().getMessage("command.housing.settings.item.toggleDamage.name"),
                getLore("command.housing.settings.item.toggleDamage.lore"),
                15));
        settingsItems.add(new SettingsItem(
                Material.CHEST,
                plugin.getMessageHandler().getMessage("command.housing.settings.item.toggleResidentAccess.name"),
                getLore("command.housing.settings.item.toggleResidentAccess.lore"),
                30));
        settingsItems.add(new SettingsItem(
                Material.SPRUCE_DOOR,
                plugin.getMessageHandler().getMessage("command.housing.settings.item.toggleInteractions.name"),
                getLore("command.housing.settings.item.toggleInteractions.lore"),
                32));

    }

    private List<TextComponent> getLore(String loreKey) {
        List<String> loreStrings = plugin.getMessageHandler().getMessageList(loreKey);
        List<TextComponent> lore = new ArrayList<>();
        for (String loreString : loreStrings) {
            lore.add((TextComponent) MiniMessage.miniMessage().deserialize(loreString));
        }
        return lore;
    }

    public void openSettingsGUI(Player player) {
        Inventory settingsGUI = Bukkit.createInventory(null, 45, title);
        for (SettingsItem settingsItem : settingsItems) {
            ItemStack item = new ItemStack(settingsItem.material, 1);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(settingsItem.name);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            List<Component> lore = new ArrayList<>(settingsItem.lore);
            meta.lore(lore);
            item.setItemMeta(meta);
            settingsGUI.setItem(settingsItem.slot, item);
        }
        player.openInventory(settingsGUI);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(title) && event.getWhoClicked() instanceof Player) {
            event.setCancelled(true);
            handleItemClick((Player) event.getWhoClicked(), event.getSlot(), event.getClick());
        }
    }

    private void handleItemClick(Player player, int slot, ClickType clickType) {
        String regionName = "housing-" + player.getUniqueId() + "-1";

        if (slot == 15) {
            toggleDamage.toggleDamage(player, regionName);
        } else if(slot == 30) {
            if (clickType == ClickType.LEFT) {
                toggleChestAccess.toggleChestAccess(player, regionName);
            } else if (clickType == ClickType.RIGHT) {
                toggleBuildAccess.toggleBuildAccess(player, regionName);
            }

        } else if (slot == 32) {
            if (clickType == ClickType.LEFT) {
                toggleDoorInteraction.toggleDoorInteraction(player, regionName);
            } else if (clickType == ClickType.RIGHT) {
                toggleTrapdoorInteraction.toggleTrapdoorInteraction(player, regionName);
            } else if (clickType == ClickType.SHIFT_LEFT) {
                toggleFenceGateInteraction.toggleFenceGateInteraction(player, regionName);
            }
        }
    }

}
