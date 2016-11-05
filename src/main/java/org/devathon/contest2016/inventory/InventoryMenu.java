package org.devathon.contest2016.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.inventory
 */
public class InventoryMenu implements Closeable {

    private static final List<InventoryMenu> menus = new ArrayList<>();

    private final Inventory inventory;
    private final InventoryItem[] items;
    private final List<Player> viewers = new ArrayList<>();

    public InventoryMenu(int size, String name) {
        items = new InventoryItem[size];
        inventory = Bukkit.createInventory(null, size, name);
        menus.add(this);
    }

    public static List<InventoryMenu> getMenus() {
        return menus;
    }

    public void open(Player player) {
        viewers.add(player);
        player.openInventory(inventory);
    }

    public void set(ItemStack item, int slot, ClickAction action) {
        items[slot] = new InventoryItem(item, action);
    }

    public void update() {

        for (int i = 0; i < items.length; i++) {

            final InventoryItem item = items[i];
            if (item == null) {
                if (inventory.getItem(i) != null)
                    inventory.setItem(i, new ItemStack(Material.AIR));
                continue;
            }
            if (inventory.getItem(i) != null && inventory.getItem(i).equals(item.getStack())) continue;

            inventory.setItem(i, item.getStack());

        }

    }

    public ClickAction getAction(int i) {
        final InventoryItem item = items[i];
        return item == null ? null : item.getAction();
        s
    }

    @Override
    public void close() throws IOException {
        menus.remove(this);
    }

    public List<Player> getViewers() {
        return viewers;
    }

    private class InventoryItem {
        private final ItemStack stack;
        private final ClickAction action;

        public InventoryItem(ItemStack stack, ClickAction action) {
            this.stack = stack;
            this.action = action;
        }

        public ItemStack getStack() {
            return stack;
        }

        public ClickAction getAction() {
            return action;
        }
    }
}
