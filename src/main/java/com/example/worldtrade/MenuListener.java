package com.example.worldtrade;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public final class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory top = e.getView().getTopInventory();
        if (!(top.getHolder() instanceof Menu menu)) return;
        e.setCancelled(true); // nothing can ever be taken out of / put into our menus
        if (!(e.getWhoClicked() instanceof Player player)) return;
        Inventory clicked = e.getClickedInventory();
        if (clicked == null) return;
        if (clicked.equals(top)) menu.onTopClick(player, e);
        else menu.onBottomClick(player, e);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof Menu) e.setCancelled(true);
    }
}
