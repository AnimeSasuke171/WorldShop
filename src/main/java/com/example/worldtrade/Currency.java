package com.example.worldtrade;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** World Currency: exchange rates and trading logic. */
public final class Currency {
    private Currency() {}

    public enum Tier {
        COPPER_EMERALD(Material.COPPER_INGOT, Material.EMERALD, 10),
        EMERALD_IRON(Material.EMERALD, Material.IRON_INGOT, 10),
        IRON_GOLD(Material.IRON_INGOT, Material.GOLD_INGOT, 10),
        GOLD_DIAMOND(Material.GOLD_INGOT, Material.DIAMOND, 10),
        DIAMOND_NETHERITE(Material.DIAMOND, Material.NETHERITE_INGOT, 36),
        NETHERITE_STAR(Material.NETHERITE_INGOT, Material.NETHER_STAR, 40),
        // Sell-only: players can turn a membrane INTO stars, but can never buy one.
        STAR_MEMBRANE(Material.NETHER_STAR, Material.PHANTOM_MEMBRANE, 16, true);

        public final Material low;
        public final Material high;
        public final int rate; // how many "low" make one "high"
        public final boolean sellOnly; // true = only high -> low is allowed

        Tier(Material low, Material high, int rate) {
            this(low, high, rate, false);
        }

        Tier(Material low, Material high, int rate, boolean sellOnly) {
            this.low = low;
            this.high = high;
            this.rate = rate;
            this.sellOnly = sellOnly;
        }
    }

    /** Value of each payout currency in copper ingots (highest first). */
    private static final Map<Material, Long> VALUE = new LinkedHashMap<>();
    static {
        VALUE.put(Material.NETHER_STAR, 14_400_000L); // 40 netherite
        VALUE.put(Material.NETHERITE_INGOT, 360_000L);
        VALUE.put(Material.DIAMOND, 10_000L);
        VALUE.put(Material.GOLD_INGOT, 1_000L);
        VALUE.put(Material.IRON_INGOT, 100L);
        VALUE.put(Material.EMERALD, 10L);
        VALUE.put(Material.COPPER_INGOT, 1L);
    }

    /** Many low -> one high. Returns number of trades done. */
    public static int upgrade(Player p, Tier t, boolean max) {
        if (t.sellOnly) return 0;
        int have = Items.count(p, t.low);
        int trades = max ? have / t.rate : (have >= t.rate ? 1 : 0);
        if (trades <= 0) return 0;
        Items.remove(p, t.low, trades * t.rate);
        Items.give(p, t.high, trades);
        return trades;
    }

    /** One high -> many low. Returns number of trades done. */
    public static int downgrade(Player p, Tier t, boolean max) {
        int have = Items.count(p, t.high);
        int trades = max ? have : Math.min(1, have);
        if (trades <= 0) return 0;
        Items.remove(p, t.high, trades);
        Items.give(p, t.low, trades * t.rate);
        return trades;
    }

    /** Pays a copper-value amount using the highest ingots possible. */
    public static void pay(Player p, long copperValue) {
        long left = copperValue;
        for (var e : VALUE.entrySet()) {
            long n = left / e.getValue();
            if (n > 0) {
                Items.give(p, e.getKey(), (int) n);
                left -= n * e.getValue();
            }
        }
    }

    public static String format(long copperValue) {
        List<String> parts = new ArrayList<>();
        long left = copperValue;
        for (var e : VALUE.entrySet()) {
            long n = left / e.getValue();
            if (n > 0) {
                parts.add(n + " " + Items.pretty(e.getKey()));
                left -= n * e.getValue();
            }
        }
        return parts.isEmpty() ? "nothing" : String.join(", ", parts);
    }
}
