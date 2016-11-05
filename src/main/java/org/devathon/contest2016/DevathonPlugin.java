package org.devathon.contest2016;

import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.devathon.contest2016.block.BlockManager;
import org.devathon.contest2016.block.impl.EnergyCollectorBlock;
import org.devathon.contest2016.builder.Builder;
import org.devathon.contest2016.builder.impl.ItemBuilder;
import org.devathon.contest2016.listener.GeneralListener;
import org.devathon.contest2016.util.Helper;

public class DevathonPlugin extends JavaPlugin {

    public static final String PREFIX = "§c[ItemMachines] §a";
    private static Helper<DevathonPlugin> helper;

    public static Helper<DevathonPlugin> helper() {
        return helper;
    }

    @Override
    public void onEnable() {
        helper = new Helper<>(this);

        BlockManager.load();


        helper.registerListener(new GeneralListener());
        helper.addRecipe(
                new ShapedRecipe(Builder.of(ItemBuilder.class).item(Material.SEA_LANTERN).name(EnergyCollectorBlock.ITEM_NAME).build())
                        .shape("ABA", "ABA", "ABA").setIngredient('A', Material.ANVIL).setIngredient('B', Material.APPLE)
        );





        // put your enable code here
    }

    @Override
    public void onDisable() {
        BlockManager.save();
    }
}

