package net.leyak.housing.commands.settings;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;

import java.util.List;

class SettingsItem {
    final Material material;
    final TextComponent name;
    final List<TextComponent> lore;
    final int slot;

    SettingsItem(Material material, TextComponent name, List<TextComponent> lore, int slot) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.slot = slot;
    }
}
