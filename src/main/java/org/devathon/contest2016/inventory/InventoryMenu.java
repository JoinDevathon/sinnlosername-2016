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
    private final List<InventoryItem> items;
    private final List<Player> viewers = new ArrayList<>();

    public InventoryMenu(int size, String name) {
        this.items = new ArrayList<>(size);
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

    public void update() {

        for (int i = 0; i < items.size(); i++) {

            final InventoryItem item = items.get(i);
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
        return items.get(i).getAction();
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
