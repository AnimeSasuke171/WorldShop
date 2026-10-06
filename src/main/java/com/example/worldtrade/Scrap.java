package com.example.worldtrade;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/** Blacksmith: what each tool/armour piece scraps into. */
public final class Scrap {
    private Scrap() {}

    public record Result(Material material, int amount) {}

    /** Material prefix of the item name -> resource it returns. */
    private static final Map<String, Material> MATERIALS = Map.of(
            "LEATHER", Material.LEATHER,
            "COPPER", Material.COPPER_INGOT,
            "IRON", Material.IRON_INGOT,
            "GOLDEN", Material.GOLD_INGOT,
            "DIAMOND", Material.DIAMOND,
            "NETHERITE", Material.NETHERITE_INGOT);

    /** Item type suffix -> amount returned. */
    public static final Map<String, Integer> YIELD = Map.of(
            "BOOTS", 3,
            "LEGGINGS", 6,
            "CHESTPLATE", 7,
            "HELMET", 4,
            "HOE", 1,
            "PICKAXE", 2,
            "AXE", 2,
            "SWORD", 1,
            "SHOVEL", 1);

    /** Returns null if the item can't be scrapped. */
    public static Result of(ItemStack item) {
        if (item == null) return null;
        String name = item.getType().name();
        int i = name.indexOf('_');
        if (i < 0) return null;
        Material out = MATERIALS.get(name.substring(0, i));
        Integer amount = YIELD.get(name.substring(i + 1));
        if (out == null || amount == null) return null;
        return new Result(out, amount * item.getAmount());
    }
}
