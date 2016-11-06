package org.devathon.contest2016.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.util
 */
public class Helper<T extends JavaPlugin> {

    private final T plugin;
    private final Reflection reflection = new Reflection();
    private final IntRedirecter redirecter = new IntRedirecter(29);

    public Helper(T instance) {
        this.plugin = instance;
        redirecter.setMap(
                1, 10, 2, 11, 3, 12, 4, 13, 5, 14, 6, 15, 7, 16,
                8, 19, 9, 20, 10, 21, 11, 22, 12, 23, 13, 24, 14, 25,
                15, 28, 16, 29, 17, 30, 18, 31, 19, 32, 20, 33, 21, 34,
                22, 37, 23, 38, 24, 39, 25, 40, 26, 41, 27, 42, 28, 43
        );
    }

    public T plugin() {
        return plugin;
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public void addRecipe(ShapedRecipe recipe) {
        Bukkit.addRecipe(recipe);
    }

    public Reflection reflection() {
        return reflection;
    }

    public boolean equals(ItemStack stack, String name, Material type) {
        return stack != null && stack.getType() == type
                && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()
                && stack.getItemMeta().getDisplayName().equals(name);
    }

    public void mid(Location location) {
        location.setX(((int) location.getX()) + 0.5);
        location.setY(((int) location.getY()) + 0.5);
        location.setZ(((int) location.getZ()) + 0.5);
    }

    public IntRedirecter redirecter() {
        return redirecter;
    }


    public String formatNumber(String num) {
        final int length = num.length();

        if (length < 4) return num;

        final int start = length % 3;
        int i = 3 - start;

        final StringBuilder b = new StringBuilder();

        for (char c : num.toCharArray()) {
            b.append(c);
            if (++i % 3 == 0) b.append('.');
        }


        return b.deleteCharAt(b.length() - 1).toString();
    }

}
