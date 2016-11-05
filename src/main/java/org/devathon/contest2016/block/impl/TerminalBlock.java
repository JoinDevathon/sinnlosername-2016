package org.devathon.contest2016.block.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.devathon.contest2016.DevathonPlugin;
import org.devathon.contest2016.block.BlockType;
import org.devathon.contest2016.block.MachineBlock;
import org.devathon.contest2016.builder.Builder;
import org.devathon.contest2016.builder.impl.ItemBuilder;
import org.devathon.contest2016.builder.impl.ThreadBuilder;
import org.devathon.contest2016.inventory.ClickAction;
import org.devathon.contest2016.inventory.InventoryMenu;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block.impl
 */
public class TerminalBlock implements MachineBlock {

    public static final String ITEM_NAME = "§6§lTerminal";
    private final Set<EnergyCollectorBlock> collectors = new HashSet<>();
    private final Set<IOModuleBlock> ioModules = new HashSet<>();

    private final int energy = 0;

    private transient BukkitTask task;
    private transient int lastEnergy = -1;
    private transient InventoryMenu menu;
    private transient Location location;

    @Override
    public void load(Location location) {
        this.location = location;
        location.getBlock().setMetadata("$blockType", new FixedMetadataValue(DevathonPlugin.helper().plugin(), type().name()));

        collectors.forEach(c -> c.setTerminal(this));
        ioModules.forEach(c -> c.setTerminal(this));


        final ItemStack greyGlass =
                Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, (short) 7).name(" ").build();

        menu = new InventoryMenu(45, "§6§lTerminal");
        menu.setSame(greyGlass, ClickAction.CANCEL, 0, 1, 2, 6, 7, 8);


        updateCounters();
        updateEnergy();

        task = Builder.of(ThreadBuilder.class).with(() -> {





        }).start(false, 20).build();

    }

    public void updateCounters() {

        // ioModule count
        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.JUKEBOX, ioModules.size()).name(IOModuleBlock.ITEM_NAME)
                .lore("§aConntected: §7" + ioModules.size()).build(), 3, ClickAction.CANCEL);

        // collector count
        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.SEA_LANTERN, collectors.size()).name(EnergyCollectorBlock.ITEM_NAME)
                .lore("§aConntected: §7" + collectors.size()).build(), 4, ClickAction.CANCEL);

        // storage count
        int i = 0;
        for (IOModuleBlock ioModule : ioModules)
            i += ioModule.getStorages().size();

        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.ENDER_CHEST, i).name(StorageBlock.ITEM_NAME)
                .lore("§aConntected: §7" + i).build(), 5, ClickAction.CANCEL);

        menu.update();
    }

    public void updateEnergy() {

        short glassType = 7;
        int height = 0;


                /*
        #0 #1 #2  #3 #4 #5  #6 #7 #8
        #9 10 11  12 13 14  15 16 17
        18 19 20  21 22 23  24 25 26
        27 28 29  30 31 32  33 34 35
        36 37 38  39 40 41  42 43 44

         */


        // 100.000.000
        if (energy > 10000000) {
            height = 4;
            glassType = 5;
        } else if (energy > 50000) {
            height = 3;
            glassType = 4;
        } else if (energy > 5000) {
            height = 1;
            glassType = 4;
        } else if (energy > 0) {
            height = 1;
            glassType = 14;
        }

        int hc = 0;

        while (hc <= height) {
            menu.set(Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, glassType)
                    .name("§a§lEnergy: §7" + energy).build(), 36 - (hc * 9), ClickAction.CANCEL);

            menu.set(Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, glassType)
                    .name("§a§lEnergy: §7" + energy).build(), 44 - (hc * 9), ClickAction.CANCEL);

            hc++;
        }

        while (hc < 4) {
            menu.set(Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, (short) 7)
                    .name("§a§lEnergy: §7" + energy).build(), 36 - (hc * 9), ClickAction.CANCEL);

            menu.set(Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, (short) 7)
                    .name("§a§lEnergy: §7" + energy).build(), 44 - (hc * 9), ClickAction.CANCEL);

            hc++;
        }


    }

    @Override
    public void place(BlockPlaceEvent e) {
        load(e.getBlock().getLocation());
        e.getPlayer().sendMessage("You placed a terminal");
    }

    @Override
    public void interact(PlayerInteractEvent e) {
        if (!e.getPlayer().isSneaking()) {
            e.setCancelled(true);

            menu.open(e.getPlayer());

        }

        e.getPlayer().sendMessage("You interacted with a terminal");
        e.getPlayer().sendMessage("This terminal is connected with " + collectors.size() + " collectors");
        e.getPlayer().sendMessage("This terminal is connected with " + ioModules.size() + " IOModules");

    }

    @Override
    public void breakBlock(BlockBreakEvent e) {

        e.getPlayer().sendMessage("You broke a terminal");
    }

    @Override
    public TerminalBlock getTerminal() {
        return this;
    }

    @Override
    public BlockType type() {
        return BlockType.TERMINAL;
    }

    @Override
    public boolean is(ItemStack itemInHand) {
        return type().is(itemInHand);
    }

    @Override
    public boolean is(Block block) {
        return block.getLocation().equals(location) &&
                block.hasMetadata("$blockType") && block.getMetadata("$blockType").get(0).asString().equals(type().name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TerminalBlock that = (TerminalBlock) o;

        return location != null ? location.equals(that.location) : that.location == null;

    }

    @Override
    public int hashCode() {
        return location != null ? location.hashCode() : 0;
    }

    public Set<EnergyCollectorBlock> getCollectors() {
        return collectors;
    }

    public Set<IOModuleBlock> getIOModules() {
        return ioModules;
    }
}
