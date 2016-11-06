package org.devathon.contest2016.builder.impl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.devathon.contest2016.builder.Builder;

import java.util.Arrays;

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

    public ItemBuilder item(ItemStack stack) {
        this.stack = stack;
        this.meta = stack.getItemMeta();
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

    public ItemBuilder lore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder glow() {
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    @Override
    public ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }
}
