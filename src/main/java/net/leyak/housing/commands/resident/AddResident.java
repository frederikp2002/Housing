package net.leyak.housing.commands.resident;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import net.leyak.housing.Housing;
import net.leyak.housing.handlers.WorldManagement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class AddResident {
    private final Housing plugin;
    private final WorldManagement worldManagement;

    public AddResident(Housing plugin) {
        this.plugin = plugin;
        this.worldManagement = new WorldManagement(plugin);
    }

    private List<Player> getPlayers(String regionName, String worldName, World world, Player owner) {
        List<Player> players = new ArrayList<>();

        ProtectedCuboidRegion region = worldManagement.getRegionsInPlayerHousing(worldName, regionName);
        plugin.getLogger().info(region.toString() + " " + region.getMembers().toString() + " " + region.getOwners().toString());
        
        for (Player player : world.getPlayers()) {
            if (!region.getMembers().contains(player.getUniqueId()) && !region.getOwners().contains(player.getUniqueId())) {
                players.add(player);
                plugin.getLogger().info(player.toString());
            }
        }

        return players;
    }

    public void openAddResidentGUI(Player owner) {
        String worldName = "housing-" + owner.getUniqueId() + "-1";
        String regionName = "housing_area";
        World world = owner.getWorld();
        List<String> lore = new ArrayList<>();
        lore.add("Click here to make this player a Resident!");
        Inventory gui = Bukkit.createInventory(null, 54, "Add Resident");
        List<Player> players = getPlayers(regionName, worldName, world, owner);

        if (players.isEmpty()) {
            NoPlayersGUI(owner);
        } else {
            for (int i = 0; i < players.size(); i++) {
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                meta.setOwningPlayer(players.get(i));
                meta.setDisplayName(players.get(i).getDisplayName());
                meta.setLore(lore);
                item.setItemMeta(meta);
                gui.setItem(i, item);
            }
            owner.openInventory(gui);
            owner.sendMessage(players.toString());
        }


    }

    public void NoPlayersGUI(Player owner) {
        List<String> lore = new ArrayList<>();
        lore.add("It doesn't seem like there are any players to add!");
        lore.add("If you want to add a player then they must be at the Housing with you!");
        Inventory gui = Bukkit.createInventory(null, 54, "Add Resident");
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("No Residents Found!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(22, item);
        owner.openInventory(gui);
    }

}
