package com.example.worldtrade;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/** Base class for all menus. A new instance is created every time a menu is opened. */
public abstract class Menu implements InventoryHolder {
    protected final WorldTradePlugin plugin;
    protected final Inventory inventory;

    protected Menu(WorldTradePlugin plugin, int size, String title) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, size, Component.text(title));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open(Player p) {
        render(p);
        p.openInventory(inventory);
    }

    protected abstract void render(Player p);

    /** Click inside the menu. */
    public abstract void onTopClick(Player p, InventoryClickEvent e);

    /** Click in the player's own inventory while the menu is open. */
    public void onBottomClick(Player p, InventoryClickEvent e) {}

    protected void fill() {
        var pane = Items.button(Material.GRAY_STAINED_GLASS_PANE, 1, " ", NamedTextColor.GRAY);
        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, pane);
    }

    protected void msg(Player p, String text, TextColor color) {
        p.sendMessage(Component.text(text, color));
    }
}
