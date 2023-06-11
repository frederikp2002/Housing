package net.leyak.housing.commands.resident;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;

import java.util.List;

public class ResidentItem {

    final Material material;
    final TextComponent name;
    final List<TextComponent> lore;
    final int slot;

    ResidentItem(Material material, TextComponent name, List<TextComponent> lore, int slot) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.slot = slot;
    }

}
