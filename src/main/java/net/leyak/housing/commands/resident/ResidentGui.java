package net.leyak.housing.commands.resident;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.leyak.housing.Housing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ResidentGui implements Listener {

    private final Housing plugin;
    private final AddResident addResident;
    private final List<ResidentItem> residentItems = new ArrayList<>();
    private final TextComponent title = Component.text("ResidentGui Settings");

    public ResidentGui(Housing plugin) {
        this.plugin = plugin;
        this.addResident = new AddResident(plugin);

        residentItems.add(new ResidentItem(
                Material.PLAYER_HEAD,
                plugin.getMessageHandler().getMessage("command.housing.settings.item.manage.name"),
                getLore("command.housing.settings.item.manage.lore"),
                12));
        residentItems.add(new ResidentItem(
                Material.BOOK,
                plugin.getMessageHandler().getMessage("command.housing.settings.item.list.name"),
                getLore("command.housing.settings.item.list.lore"),
                14));

    }

    private List<TextComponent> getLore(String loreKey) {
        List<String> loreStrings = plugin.getMessageHandler().getMessageList(loreKey);
        List<TextComponent> lore = new ArrayList<>();
        for (String loreString : loreStrings) {
            lore.add((TextComponent) MiniMessage.miniMessage().deserialize(loreString));
        }
        return lore;
    }

    public void openResidentGUI(Player player) {
        Inventory residentGUI = Bukkit.createInventory(null, 27, title);
        for (ResidentItem residentItem : residentItems) {
            ItemStack item = new ItemStack(residentItem.material, 1);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(residentItem.name);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            List<Component> lore = new ArrayList<>(residentItem.lore);
            meta.lore(lore);
            item.setItemMeta(meta);
            residentGUI.setItem(residentItem.slot, item);
        }
        player.openInventory(residentGUI);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(title) && event.getWhoClicked() instanceof Player) {
            event.setCancelled(true);
            handleItemClick((Player) event.getWhoClicked(), event.getSlot());
        }
    }

    private void handleItemClick(Player player, int slot) {
        if (slot == 12) {
            player.closeInventory();
             addResident.openAddResidentGUI(player);
        } else if(slot == 30) {
            player.sendMessage("ResidentGui Access");
        }
    }

}
