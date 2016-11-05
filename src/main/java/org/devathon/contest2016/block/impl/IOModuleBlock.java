package org.devathon.contest2016.block.impl;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.devathon.contest2016.DevathonPlugin;
import org.devathon.contest2016.block.BlockManager;
import org.devathon.contest2016.block.BlockType;
import org.devathon.contest2016.block.MachineBlock;
import org.devathon.contest2016.block.SerializeableLocation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block.impl
 */
public class IOModuleBlock implements MachineBlock {

    public static final String ITEM_NAME = "§6§lIO Module";

    private short frequency = -1;
    private Set<StorageBlock> storages = new HashSet<>();

    private transient TerminalBlock terminal;
    private transient Location location;


    @Override
    public void load(Location location) {
        this.location = location;
        location.getBlock().setMetadata("$blockType", new FixedMetadataValue(DevathonPlugin.helper().plugin(), type().name()));

        storages.forEach(s -> s.setIoModule(this));
    }

    @Override
    public void interact(PlayerInteractEvent e) {
        if (!e.getPlayer().isSneaking()) e.setCancelled(true);

        e.getPlayer().sendMessage("i with IOModule");

    }

    @Override
    public void place(BlockPlaceEvent e) {

        boolean found = false;

        frequency = Short.parseShort(e.getItemInHand().getItemMeta().getLore().get(0).substring(15).trim());

        for (Map.Entry<SerializeableLocation, MachineBlock> entry : BlockManager.getInstance().getBlocks().entrySet()) {

            if (!(entry.getValue() instanceof IOModuleBlock)) continue;
            final IOModuleBlock block = (IOModuleBlock) entry.getValue();
            if (block.frequency != frequency) continue;
            if (block.getTerminal() == null) continue;

            found = true;
            terminal = block.getTerminal();

        }

        final MachineBlock against = BlockManager.getInstance().getBlocks()
                .get(new SerializeableLocation(e.getBlockAgainst().getLocation()));

        if (!found && (against == null || !(against instanceof TerminalBlock))) {
            BlockManager.getInstance().remove(e.getBlock().getLocation());
            e.setCancelled(true);
            e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "§cCan't find a terminal to connect.");
            return;
        } else if (against != null && against instanceof TerminalBlock) {
            terminal = against.getTerminal();
        }

        terminal.getIOModules().add(this);
        terminal.updateCounters();

        e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "Successfully connected!");

        load(e.getBlock().getLocation());

    }

    @Override
    public void breakBlock(BlockBreakEvent e) {
        e.getPlayer().sendMessage("br IOModule");
        terminal.getIOModules().remove(this);
        terminal.updateCounters();
    }

    @Override
    public TerminalBlock getTerminal() {
        return terminal;
    }

    public void setTerminal(TerminalBlock terminal) {
        this.terminal = terminal;
    }

    @Override
    public BlockType type() {
        return BlockType.IO_MODULE;
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

    public Set<StorageBlock> getStorages() {
        return storages;
    }
}
