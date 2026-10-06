package com.example.worldtrade;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

/** World Trade: food prices (loaded from config.yml) and selling logic. */
public final class Market {
    public static final int MAX_ITEMS = 45;

    /** Foods players already have an incentive to make, so we never buy them. */
    private static final Set<Material> EXCLUDED =
            EnumSet.of(Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE, Material.GOLDEN_CARROT);

    public record Sale(int items, long value) {}

    private final Map<Material, Long> prices = new LinkedHashMap<>();

    public void load(WorldTradePlugin plugin) {
        prices.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("prices");
        if (section == null) return;

        List<Map.Entry<Material, Long>> list = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            Material m = Material.matchMaterial(key);
            if (m == null || !m.isItem()) {
                plugin.getLogger().warning("Unknown item in prices: " + key);
            } else if (EXCLUDED.contains(m)) {
                plugin.getLogger().warning(key + " is excluded from World Trade and was ignored.");
            } else {
                long price = section.getLong(key);
                if (price > 0) list.add(Map.entry(m, price));
            }
        }
        list.sort(Map.Entry.<Material, Long>comparingByValue().thenComparing(e -> e.getKey().name()));
        for (var e : list) prices.put(e.getKey(), e.getValue());
        if (prices.size() > MAX_ITEMS) {
            plugin.getLogger().warning("Only the first " + MAX_ITEMS + " cheapest items fit in the menu.");
        }
    }

    public List<Material> items() {
        return prices.keySet().stream().limit(MAX_ITEMS).toList();
    }

    public long price(Material m) {
        return prices.getOrDefault(m, 0L);
    }

    /** Removes the items from the player; the caller pays out the total. */
    public Sale sell(Player p, Material m, boolean all) {
        int have = Items.count(p, m);
        int n = all ? have : Math.min(1, have);
        if (n <= 0) return new Sale(0, 0);
        Items.remove(p, m, n);
        return new Sale(n, price(m) * n);
    }
}
