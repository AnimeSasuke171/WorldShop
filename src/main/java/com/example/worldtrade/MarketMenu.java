package com.example.worldtrade;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public final class MarketMenu extends Menu {
    private static final int BACK = 45;
    private static final int SELL_ALL = 49;

    private final List<Material> shown;

    public MarketMenu(WorldTradePlugin plugin) {
        super(plugin, 54, "World Trade");
        this.shown = plugin.market().items();
    }

    @Override
    protected void render(Player p) {
        fill();
        Market market = plugin.market();
        for (int i = 0; i < shown.size(); i++) {
            Material m = shown.get(i);
            inventory.setItem(i, Items.button(m, 1, Items.pretty(m), NamedTextColor.GOLD,
                    "Pays: " + Currency.format(market.price(m)) + " each",
                    "You have: " + Items.count(p, m),
                    "",
                    "Click: sell 1",
                    "Shift-click: sell all"));
        }
        inventory.setItem(BACK, Items.button(Material.ARROW, 1, "Back", NamedTextColor.YELLOW));
        inventory.setItem(SELL_ALL, Items.button(Material.HOPPER, 1, "Sell everything", NamedTextColor.RED,
                "Sells all food listed here", "from your inventory."));
    }

    @Override
    public void onTopClick(Player p, InventoryClickEvent e) {
        int slot = e.getSlot();
        Market market = plugin.market();

        if (slot == BACK) {
            new MainMenu(plugin).open(p);
        } else if (slot == SELL_ALL) {
            int items = 0;
            long total = 0;
            for (Material m : shown) {
                Market.Sale s = market.sell(p, m, true);
                items += s.items();
                total += s.value();
            }
            payAndReport(p, items, total);
        } else if (slot >= 0 && slot < shown.size()) {
            Material m = shown.get(slot);
            Market.Sale s = market.sell(p, m, e.isShiftClick());
            if (s.items() == 0) msg(p, "You don't have any " + Items.pretty(m) + ".", NamedTextColor.RED);
            else payAndReport(p, s.items(), s.value());
        } else {
            return;
        }
        render(p);
    }

    private void payAndReport(Player p, int items, long total) {
        if (items == 0) {
            msg(p, "Nothing to sell.", NamedTextColor.RED);
            return;
        }
        Currency.pay(p, total);
        msg(p, "Sold " + items + " item(s) for " + Currency.format(total) + ".", NamedTextColor.GREEN);
    }
}
