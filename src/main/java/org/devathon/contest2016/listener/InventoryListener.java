package org.devathon.contest2016.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.devathon.contest2016.inventory.ClickAction;
import org.devathon.contest2016.inventory.InventoryMenu;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.listener
 */
public class InventoryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {

        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getName() == null) return;

        for (InventoryMenu menu : InventoryMenu.getMenus()) {
            if (!menu.getViewers().contains(e.getWhoClicked()) || !menu.getName().equals(e.getClickedInventory().getName())) continue;

            final ClickAction action = menu.getAction(e.getSlot());

            if (action == null) {
                menu.getDefaultAction().click(e);
                continue;
            }

            action.click(e);
        }


    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClose(InventoryCloseEvent e) {
        for (InventoryMenu menu : InventoryMenu.getMenus()) {
            if (!menu.getViewers().contains(e.getPlayer())) continue;

            menu.getViewers().remove(e.getPlayer());

        }

    }


}
