package org.devathon.contest2016.block.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.devathon.contest2016.DevathonPlugin;
import org.devathon.contest2016.block.BlockType;
import org.devathon.contest2016.block.MachineBlock;
import org.devathon.contest2016.builder.Builder;
import org.devathon.contest2016.builder.impl.ItemBuilder;
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

    private final long energy = 0;

    private final transient InventoryMenu menu = new InventoryMenu(45, "§6§lTerminal");
    private transient Location location;

    @Override
    public void load(Location location) {
        this.location = location;
        location.getBlock().setMetadata("$blockType", new FixedMetadataValue(DevathonPlugin.helper().plugin(), type().name()));

        collectors.forEach(c -> c.setTerminal(this));
        ioModules.forEach(c -> c.setTerminal(this));

        /*
        0 1 2  3 4 5  6 7 8

         */

        updateCounters();


    }

    private void updateCounters() {

        // ioModule count
        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.JUKEBOX, ioModules.size()).name(IOModuleBlock.ITEM_NAME).build(), 3, ClickAction.CANCEL);

        // collector count
        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.SEA_LANTERN, collectors.size()).name(EnergyCollectorBlock.ITEM_NAME).build(), 4, ClickAction.CANCEL);

        // storage count
        int i = 0;
        for (IOModuleBlock ioModule : ioModules)
            i += ioModule.getStorages().size();

        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.ENDER_CHEST, i).name(StorageBlock.ITEM_NAME).build(), 5, ClickAction.CANCEL);

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
