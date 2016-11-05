package org.devathon.contest2016;

import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.devathon.contest2016.block.BlockManager;
import org.devathon.contest2016.builder.Builder;
import org.devathon.contest2016.builder.impl.ItemBuilder;
import org.devathon.contest2016.util.Helper;

public class DevathonPlugin extends JavaPlugin {

    private static Helper<DevathonPlugin> helper;

    public static Helper<DevathonPlugin> helper() {
        return helper;
    }

    @Override
    public void onEnable() {
        helper = new Helper<>(this);

        BlockManager.load();


        helper.addRecipe(
                new ShapedRecipe(Builder.of(ItemBuilder.class).item(Material.IRON_BLOCK).name("kappa").build())
                        .shape("ABA", "ABA", "ABA").setIngredient('A', Material.ANVIL).setIngredient('B', Material.APPLE)
        );





        // put your enable code here
    }

    @Override
    public void onDisable() {
        BlockManager.save();
    }
}

