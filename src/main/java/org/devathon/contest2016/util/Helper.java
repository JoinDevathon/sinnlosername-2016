package org.devathon.contest2016.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.util
 */
public class Helper<T extends JavaPlugin> {

    private final T plugin;
    private final Reflection reflection = new Reflection();

    public Helper(T instance) {
        this.plugin = instance;
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

}
