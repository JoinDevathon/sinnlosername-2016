package org.devathon.contest2016.builder.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.devathon.contest2016.builder.Builder;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.builder.impl
 */
public class ItemBuilder implements Builder<ItemStack> {

    private ItemStack stack;
    private ItemMeta meta;

    public ItemBuilder item(Material m) {
        stack = new ItemStack(m);
        meta = stack.getItemMeta();
        return this;
    }

    public ItemBuilder item(Material m, int amount) {
        stack = new ItemStack(m, amount);
        meta = stack.getItemMeta();
        return this;
    }

    public ItemBuilder item(Material m, int amount, short data) {
        stack = new ItemStack(m, amount, data);
        meta = stack.getItemMeta();
        return this;
    }

    public ItemBuilder name(String s) {
        meta.setDisplayName(s);
        return this;
    }


    @Override
    public ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }
}
