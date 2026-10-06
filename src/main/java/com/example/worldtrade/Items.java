package com.example.worldtrade;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Locale;

/** Inventory + item helper methods. */
public final class Items {
    private Items() {}

    public static Component text(String s, TextColor color) {
        return Component.text(s, color).decoration(TextDecoration.ITALIC, false);
    }

    public static ItemStack button(Material material, int amount, String name, TextColor color, String... lore) {
        ItemStack item = new ItemStack(material, Math.max(1, Math.min(amount, 64)));
        item.editMeta(meta -> {
            meta.displayName(text(name, color));
            if (lore.length > 0) {
                meta.lore(Arrays.stream(lore).map(l -> text(l, NamedTextColor.GRAY)).toList());
            }
        });
        return item;
    }

    public static String pretty(Material m) {
        StringBuilder sb = new StringBuilder();
        for (String w : m.name().toLowerCase(Locale.ROOT).split("_")) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
        }
        return sb.toString();
    }

    /** True if the item has a custom name, lore or enchantments (we never touch those as currency/food). */
    public static boolean isCustomised(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta m = item.getItemMeta();
        return m.hasDisplayName() || m.hasLore() || m.hasEnchants();
    }

    public static boolean isPlain(ItemStack item, Material material) {
        return item != null && item.getType() == material && !isCustomised(item);
    }

    public static int count(Player p, Material material) {
        int total = 0;
        for (ItemStack s : p.getInventory().getStorageContents()) {
            if (isPlain(s, material)) total += s.getAmount();
        }
        return total;
    }

    public static void remove(Player p, Material material, int amount) {
        Inventory inv = p.getInventory();
        ItemStack[] contents = inv.getStorageContents();
        int left = amount;
        for (int i = 0; i < contents.length && left > 0; i++) {
            ItemStack s = contents[i];
            if (!isPlain(s, material)) continue;
            int take = Math.min(left, s.getAmount());
            if (take == s.getAmount()) contents[i] = null;
            else s.setAmount(s.getAmount() - take);
            left -= take;
        }
        inv.setStorageContents(contents);
    }

    /** Gives items, splitting stacks; anything that doesn't fit is dropped at the player's feet. */
    public static void give(Player p, Material material, int amount) {
        int max = material.getMaxStackSize();
        while (amount > 0) {
            int n = Math.min(max, amount);
            p.getInventory().addItem(new ItemStack(material, n))
                    .values()
                    .forEach(left -> p.getWorld().dropItemNaturally(p.getLocation(), left));
            amount -= n;
        }
    }
}
