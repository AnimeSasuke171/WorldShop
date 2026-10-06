package com.example.worldtrade;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class CurrencyMenu extends Menu {
    private static final int UP_START = 10;   // row 2: many low -> 1 high
    private static final int DOWN_START = 19; // row 3: 1 high -> many low
    private static final int BACK = 31;

    public CurrencyMenu(WorldTradePlugin plugin) {
        super(plugin, 36, "World Currency");
    }

    @Override
    protected void render(Player p) {
        fill();
        Currency.Tier[] tiers = Currency.Tier.values();
        for (int i = 0; i < tiers.length; i++) {
            Currency.Tier t = tiers[i];
            String lo = Items.pretty(t.low);
            String hi = Items.pretty(t.high);

            if (t.sellOnly) {
                inventory.setItem(UP_START + i, Items.button(Material.BARRIER, 1,
                        hi + " can't be bought", NamedTextColor.DARK_RED,
                        "It can only be sold for " + t.rate + " " + lo + "."));
            } else {
                inventory.setItem(UP_START + i, Items.button(t.high, 1,
                        t.rate + " " + lo + " -> 1 " + hi, NamedTextColor.GREEN,
                        "Click: trade once", "Shift-click: trade as many as possible",
                        "You have: " + Items.count(p, t.low) + " " + lo));
            }

            inventory.setItem(DOWN_START + i, Items.button(t.low, t.rate,
                    "1 " + hi + " -> " + t.rate + " " + lo, NamedTextColor.RED,
                    "Click: trade once", "Shift-click: trade as many as possible",
                    "You have: " + Items.count(p, t.high) + " " + hi));
        }
        inventory.setItem(BACK, Items.button(Material.ARROW, 1, "Back", NamedTextColor.YELLOW));
    }

    @Override
    public void onTopClick(Player p, InventoryClickEvent e) {
        int slot = e.getSlot();
        boolean max = e.isShiftClick();
        Currency.Tier[] tiers = Currency.Tier.values();

        if (slot == BACK) {
            new MainMenu(plugin).open(p);
            return;
        }
        if (slot >= UP_START && slot < UP_START + tiers.length) {
            Currency.Tier t = tiers[slot - UP_START];
            if (t.sellOnly) {
                msg(p, Items.pretty(t.high) + " can't be bought, only sold.", NamedTextColor.RED);
                return;
            }
            int n = Currency.upgrade(p, t, max);
            if (n == 0) msg(p, "You need " + t.rate + " " + Items.pretty(t.low) + ".", NamedTextColor.RED);
            else msg(p, "Traded " + (n * t.rate) + " " + Items.pretty(t.low) + " for " + n + " "
                    + Items.pretty(t.high) + ".", NamedTextColor.GREEN);
        } else if (slot >= DOWN_START && slot < DOWN_START + tiers.length) {
            Currency.Tier t = tiers[slot - DOWN_START];
            int n = Currency.downgrade(p, t, max);
            if (n == 0) msg(p, "You need 1 " + Items.pretty(t.high) + ".", NamedTextColor.RED);
            else msg(p, "Traded " + n + " " + Items.pretty(t.high) + " for " + (n * t.rate) + " "
                    + Items.pretty(t.low) + ".", NamedTextColor.GREEN);
        } else {
            return;
        }
        render(p);
    }
}
