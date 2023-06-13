package net.leyak.housing.commands.resident;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.leyak.housing.Housing;
import net.leyak.housing.handlers.WorldManagement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class RemoveResident implements Listener {

    private final Housing plugin;
    private final WorldManagement worldManagement;

    public RemoveResident(Housing plugin) {
        this.plugin = plugin;
        this.worldManagement = new WorldManagement(plugin);
    }

    private List<Player> getPlayers(String regionName, String worldName, World world) {
        List<Player> players = new ArrayList<>();

        ProtectedCuboidRegion region = worldManagement.getRegionsInPlayerHousing(worldName, regionName);

        for (Player player : world.getPlayers()) {
            if (region.getMembers().contains(player.getUniqueId()) && !region.getOwners().contains(player.getUniqueId())) {
                players.add(player);
            }
        }

        return players;
    }

    private List<TextComponent> getLore(String loreKey) {
        List<String> loreStrings = plugin.getMessageHandler().getMessageList(loreKey);
        List<TextComponent> lore = new ArrayList<>();
        for (String loreString : loreStrings) {
            lore.add((TextComponent) MiniMessage.miniMessage().deserialize(loreString));
        }
        return lore;
    }

    public void openRemoveResidentGUI(Player owner) {
        String worldName = "housing-" + owner.getUniqueId() + "-1";
        String regionName = "housing_area";
        World world = owner.getWorld();
        List<Component> lore = new ArrayList<>(getLore("commands.housing.resident.removeResident.item.lore"));
        Inventory gui = Bukkit.createInventory(null, 54, "Remove Resident");
        List<Player> players = getPlayers(regionName, worldName, world);

        if (players.isEmpty()) {
            NoPlayersGUI(owner);
        } else {
            for (int i = 0; i < players.size(); i++) {
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                meta.setOwningPlayer(players.get(i));
                meta.displayName(plugin.getMessageHandler().getMessage("commands.housing.resident.removeResident.item.name", players.get(i).getName()));
                meta.lore(lore);
                item.setItemMeta(meta);
                gui.setItem(i, item);
            }
            owner.openInventory(gui);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player clicker = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();

        // check if the clicked inventory is the "Remove Resident" GUI
        if (event.getView().getTitle().equals("Remove Resident")) {
            // make sure the clicked slot was not empty
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                // make sure the clicked item is a player head
                if (clickedItem.getType() == Material.PLAYER_HEAD) {
                    SkullMeta clickedMeta = (SkullMeta) clickedItem.getItemMeta();
                    OfflinePlayer target = clickedMeta.getOwningPlayer();
                    if (target != null) {
                        String worldName = "housing-" + clicker.getUniqueId() + "-1";
                        String regionName = "housing_area";

                        // Remove the target player from the WorldGuard region
                        removePlayerFromRegion(regionName, worldName, target);
                        clicker.sendMessage(plugin.getMessageHandler().getMessage("commands.housing.resident.removeResident.success", target.getName()));

                        // Get all items in the inventory
                        List<ItemStack> items = new ArrayList<>();
                        for (ItemStack item : clickedInventory.getContents()) {
                            if (item != null && item.getType() != Material.AIR) {
                                items.add(item);
                            }
                        }

                        // Remove the selected item from the list
                        items.remove(clickedItem);

                        // Clear the inventory and add the items back
                        clickedInventory.clear();
                        for (int i = 0; i < items.size(); i++) {
                            clickedInventory.setItem(i, items.get(i));
                        }
                    }
                }
            }
            event.setCancelled(true); // to prevent the player from taking the item
        }
    }

    private void removePlayerFromRegion(String regionName, String worldName, OfflinePlayer target) {
        ProtectedCuboidRegion region = worldManagement.getRegionsInPlayerHousing(worldName, regionName);
        region.getMembers().removePlayer(target.getUniqueId());
    }

    public void NoPlayersGUI(Player owner) {
        List<Component> lore = new ArrayList<>(getLore("commands.housing.resident.removeResident.noPlayersFound.item.lore"));
        Inventory gui = Bukkit.createInventory(null, 54, "Remove Resident");
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(plugin.getMessageHandler().getMessage("commands.housing.resident.removeResident.noPlayersFound.item.name"));
        meta.lore(lore);
        item.setItemMeta(meta);
        gui.setItem(22, item);
        owner.openInventory(gui);
    }

}
