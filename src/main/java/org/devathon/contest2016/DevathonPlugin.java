package org.devathon.contest2016;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
import org.devathon.contest2016.listener.InventoryListener;
import org.devathon.contest2016.util.Helper;

public class DevathonPlugin extends JavaPlugin {

    public static final String PREFIX = "§8[§c§lItemMachines§8] §a";
    public static final String CRYSTAL_NAME = "§6§lEnergy Crystal";
    private static Helper<DevathonPlugin> helper;

    public static Helper<DevathonPlugin> helper() {
        return helper;
    }

    @Override
    public void onEnable() {
        helper = new Helper<>(this);

        BlockManager.load();

        helper.registerListener(new GeneralListener());
        helper.registerListener(new InventoryListener());

        helper.addRecipe(
                new ShapedRecipe(Builder.of(ItemBuilder.class).item(Material.SEA_LANTERN).name(EnergyCollectorBlock.ITEM_NAME).build())
                        .shape("SGS", "GDG", "SGS").setIngredient('S', Material.SEA_LANTERN)
                        .setIngredient('G', Material.GOLD_BLOCK).setIngredient('D', Material.DIAMOND)
        );

        helper.addRecipe(
                new ShapedRecipe(Builder.of(ItemBuilder.class).item(Material.WORKBENCH).name(TerminalBlock.ITEM_NAME).build())
                        .shape("DGD", "OSO", "SDS").setIngredient('D', Material.DIAMOND).setIngredient('O', Material.OBSIDIAN)
                        .setIngredient('G', Material.GOLD_INGOT).setIngredient('S', Material.REDSTONE_BLOCK)
        );

        helper.addRecipe(
                new ShapedRecipe(Builder.of(ItemBuilder.class).item(Material.JUKEBOX).name(IOModuleBlock.ITEM_NAME).lore("§aFrequency:§7 0").build())
                        .shape("RRR", "DGD", "RRR")
                        .setIngredient('R', Material.REDSTONE_BLOCK).setIngredient('G', Material.GLASS)
                        .setIngredient('D', Material.DIAMOND)
        );

        helper.addRecipe(
                new ShapedRecipe(Builder.of(ItemBuilder.class).item(Material.ENDER_CHEST).name(StorageBlock.ITEM_NAME).build())
                        .shape("GRG", "RER", "GRG").setIngredient('G', Material.GOLD_INGOT)
                        .setIngredient('R', Material.REDSTONE).setIngredient('E', Material.ENDER_CHEST)
        );

        helper.addRecipe(
                new ShapedRecipe(Builder.of(ItemBuilder.class).item(Material.DIAMOND).name(CRYSTAL_NAME).glow().build())
                        .shape("RDR", "DOD", "RDR").setIngredient('R', Material.REDSTONE_BLOCK).setIngredient('D', Material.DIAMOND)
                        .setIngredient('O', Material.OBSIDIAN)
        );

        helper.registerListener(new Listener() {
            @EventHandler
            public void onChat(AsyncPlayerChatEvent e) {
                e.setCancelled(true);
                Bukkit.getScheduler().runTask(helper().plugin(), () -> {
                    e.getPlayer().getInventory().addItem(Builder.of(ItemBuilder.class).item(Material.SEA_LANTERN).name(EnergyCollectorBlock.ITEM_NAME).build());
                    e.getPlayer().getInventory().addItem(Builder.of(ItemBuilder.class).item(Material.WORKBENCH).name(TerminalBlock.ITEM_NAME).build());
                    e.getPlayer().getInventory().addItem(Builder.of(ItemBuilder.class).item(Material.JUKEBOX).name(IOModuleBlock.ITEM_NAME).lore("§aFrequency:§7 0").build());
                    e.getPlayer().getInventory().addItem(Builder.of(ItemBuilder.class).item(Material.ENDER_CHEST).name(StorageBlock.ITEM_NAME).build());

                });

            }
        });

//A



        // put your enable code here
    }

    @Override
    public void onDisable() {
        BlockManager.save();
    }
}

