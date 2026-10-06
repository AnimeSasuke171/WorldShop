package com.example.worldtrade;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public final class BlacksmithMenu extends Menu {
    private static final int INFO = 11;
    private static final int SCRAP_ALL = 15;
    private static final int BACK = 22;

    public BlacksmithMenu(WorldTradePlugin plugin) {
        super(plugin, 27, "Blacksmith");
    }

    @Override
    protected void render(Player p) {
        fill();
        inventory.setItem(INFO, Items.button(Material.BOOK, 1, "How it works", NamedTextColor.AQUA,
                "Click any tool or armour in YOUR inventory",
                "to scrap it into its material.",
                "",
                "Boots: " + Scrap.YIELD.get("BOOTS"),
                "Leggings: " + Scrap.YIELD.get("LEGGINGS"),
                "Chestplate: " + Scrap.YIELD.get("CHESTPLATE"),
                "Helmet: " + Scrap.YIELD.get("HELMET"),
                "Hoe: " + Scrap.YIELD.get("HOE"),
                "Pickaxe: " + Scrap.YIELD.get("PICKAXE"),
                "Axe: " + Scrap.YIELD.get("AXE"),
                "Sword: " + Scrap.YIELD.get("SWORD"),
                "Shovel: " + Scrap.YIELD.get("SHOVEL"),
                "",
                "Enchanted / renamed items are never scrapped."));
        inventory.setItem(SCRAP_ALL, Items.button(Material.GRINDSTONE, 1, "Scrap everything", NamedTextColor.RED,
                "Scraps every plain tool and armour piece",
                "in your inventory (not worn armour)."));
        inventory.setItem(BACK, Items.button(Material.ARROW, 1, "Back", NamedTextColor.YELLOW));
    }

    @Override
    public void onTopClick(Player p, InventoryClickEvent e) {
        if (e.getSlot() == BACK) new MainMenu(plugin).open(p);
        else if (e.getSlot() == SCRAP_ALL) scrapAll(p);
    }

    @Override
    public void onBottomClick(Player p, InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        Scrap.Result r = Scrap.of(item);
        if (r == null) {
            msg(p, "The blacksmith can't scrap that.", NamedTextColor.RED);
            return;
        }
        if (Items.isCustomised(item)) {
            msg(p, "That item is enchanted or renamed - the blacksmith refuses.", NamedTextColor.RED);
            return;
        }
        String name = Items.pretty(item.getType());
        e.setCurrentItem(null);
        Items.give(p, r.material(), r.amount());
        msg(p, "Scrapped " + name + " into " + r.amount() + " " + Items.pretty(r.material()) + ".",
                NamedTextColor.GREEN);
    }

    private void scrapAll(Player p) {
        ItemStack[] contents = p.getInventory().getStorageContents();
        Map<Material, Integer> gained = new EnumMap<>(Material.class);
        for (int i = 0; i < contents.length; i++) {
            Scrap.Result r = Scrap.of(contents[i]);
            if (r == null || Items.isCustomised(contents[i])) continue;
            contents[i] = null;
            gained.merge(r.material(), r.amount(), Integer::sum);
        }
        if (gained.isEmpty()) {
            msg(p, "Nothing to scrap.", NamedTextColor.RED);
            return;
        }
        p.getInventory().setStorageContents(contents);
        gained.forEach((m, n) -> Items.give(p, m, n));
        StringBuilder sb = new StringBuilder("Scrapped for: ");
        gained.forEach((m, n) -> sb.append(n).append(' ').append(Items.pretty(m)).append(", "));
        msg(p, sb.substring(0, sb.length() - 2), NamedTextColor.GREEN);
    }
}
