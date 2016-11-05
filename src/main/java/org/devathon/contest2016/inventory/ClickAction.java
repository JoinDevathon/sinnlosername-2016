package org.devathon.contest2016.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.inventory
 */
public interface ClickAction {
    ClickAction CANCEL = e -> e.setCancelled(true);
    ClickAction NOTHING = e -> {
    };

    void click(InventoryClickEvent e);
}
