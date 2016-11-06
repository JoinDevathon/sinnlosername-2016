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

        e.getWhoClicked().sendMessage("trigger");

        if (e.getClickedInventory() == null || e.getClickedInventory().getName() == null) {
            e.getWhoClicked().sendMessage("fuck");
            if (e.getInventory() != null && e.getInventory().getName() != null && e.getInventory().getName().equals("§6§lTerminal"))
                e.setCancelled(true);
            return;
        }

        e.getWhoClicked().sendMessage(e.getClickedInventory().getName());
        e.getWhoClicked().sendMessage(e.getInventory().getName());


        for (InventoryMenu menu : InventoryMenu.getMenus()) {
            if (!menu.getName().equals(e.getClickedInventory().getName()) && menu.getName().equals(e.getInventory().getName())) {
                menu.getClickOtherAction().click(e);
                continue;
            }

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
