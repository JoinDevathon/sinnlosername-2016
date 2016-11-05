package org.devathon.contest2016;

import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.devathon.contest2016.block.BlockManager;
import org.devathon.contest2016.block.impl.EnergyCollectorBlock;
import org.devathon.contest2016.block.impl.IOModuleBlock;
import org.devathon.contest2016.block.impl.StorageBlock;
import org.devathon.contest2016.block.impl.TerminalBlock;
import org.devathon.contest2016.builder.Builder;
import org.devathon.contest2016.builder.impl.ItemBuilder;
import org.devathon.contest2016.listener.GeneralListener;
import org.devathon.contest2016.util.Helper;

public class DevathonPlugin extends JavaPlugin {

    public static final String PREFIX = "§8[§c§lItemMachines§8] §a";
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

        helper.addRecipe(
                new ShapedRecipe(Builder.of(ItemBuilder.class).item(Material.WORKBENCH).name(TerminalBlock.ITEM_NAME).build())
                        .shape("ABA", "ABA", "ABB").setIngredient('A', Material.ANVIL).setIngredient('B', Material.APPLE)
        );

        helper.addRecipe(
                new ShapedRecipe(Builder.of(ItemBuilder.class).item(Material.JUKEBOX).name(IOModuleBlock.ITEM_NAME).lore("§aFrequency:§7 0").build())
                        .shape("ABA", "ABA", "BBB").setIngredient('A', Material.ANVIL).setIngredient('B', Material.APPLE)
        );

        helper.addRecipe(
                new ShapedRecipe(Builder.of(ItemBuilder.class).item(Material.ENDER_CHEST).name(StorageBlock.ITEM_NAME).build())
                        .shape("ABA", "ABA", "BAB").setIngredient('A', Material.ANVIL).setIngredient('B', Material.APPLE)
        );





//A



        // put your enable code here
    }

    @Override
    public void onDisable() {
        BlockManager.save();
    }
}

