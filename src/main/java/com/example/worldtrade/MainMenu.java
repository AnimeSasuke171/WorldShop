package com.example.worldtrade;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class MainMenu extends Menu {

    public MainMenu(WorldTradePlugin plugin) {
        super(plugin, 27, "World Trade");
    }

    @Override
    protected void render(Player p) {
        fill();
        inventory.setItem(11, Items.button(Material.GOLD_INGOT, 1, "World Currency", NamedTextColor.GOLD,
                "Exchange copper, iron, gold,", "diamond, netherite and nether stars", "- up and down."));
        inventory.setItem(13, Items.button(Material.ANVIL, 1, "Blacksmith", NamedTextColor.GRAY,
                "Scrap tools and armour", "back into their raw materials."));
        inventory.setItem(15, Items.button(Material.HAY_BLOCK, 1, "World Trade", NamedTextColor.GREEN,
                "Sell farmed & cooked food", "for a premium price."));
    }

    @Override
    public void onTopClick(Player p, InventoryClickEvent e) {
        switch (e.getSlot()) {
            case 11 -> new CurrencyMenu(plugin).open(p);
            case 13 -> new BlacksmithMenu(plugin).open(p);
            case 15 -> new MarketMenu(plugin).open(p);
            default -> {}
        }
    }
}
