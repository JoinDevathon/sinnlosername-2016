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

import java.util.Map;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block.impl
 */
public class IOModuleBlock implements MachineBlock {

    private short frequency;

    private transient TerminalBlock terminal;
    private transient Location location;

    @Override
    public void load(Location location) {
        this.location = location;
        location.getBlock().setMetadata("$blockType", new FixedMetadataValue(DevathonPlugin.helper().plugin(), type().name()));

    }

    @Override
    public void interact(PlayerInteractEvent e) {

    }

    @Override
    public void place(BlockPlaceEvent e) {

        boolean found = false;

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

        e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "Successfully connected!");

    }

    @Override
    public void breakBlock(BlockBreakEvent e) {

    }

    @Override
    public TerminalBlock getTerminal() {
        return terminal;
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

}
