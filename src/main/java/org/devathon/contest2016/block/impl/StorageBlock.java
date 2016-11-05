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

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block.impl
 */
public class StorageBlock implements MachineBlock {

    public static final String ITEM_NAME = "§6§lStorage";

    private transient Location location;
    private transient IOModuleBlock ioModule;

    @Override
    public void load(Location location) {
        this.location = location;
        location.getBlock().setMetadata("$blockType", new FixedMetadataValue(DevathonPlugin.helper().plugin(), type().name()));

    }

    @Override
    public void interact(PlayerInteractEvent e) {
        e.setCancelled(!e.getPlayer().isSneaking());

        e.getPlayer().sendMessage("omfg you interacted with an storage block! gg");

    }

    @Override
    public void place(BlockPlaceEvent e) {

        final BlockType type = BlockType.of(e.getBlockAgainst());

        if (type == null || type != BlockType.IO_MODULE) {

            BlockManager.getInstance().remove(e.getBlock().getLocation());
            e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "§cYou can only place a storage block against an IOModule!");
            e.setCancelled(true);

            return;
        }

        load(e.getBlock().getLocation());

        ioModule = (IOModuleBlock) BlockManager.getInstance().getBlocks()
                .get(new SerializeableLocation(e.getBlockAgainst().getLocation()));

        ioModule.getStorages().add(this);

        e.getPlayer().sendMessage("You placed a storage block!");

    }

    @Override
    public void breakBlock(BlockBreakEvent e) {
        e.getPlayer().sendMessage("You broke a storage block!");

        ioModule.getStorages().remove(this);

    }

    @Override
    public TerminalBlock getTerminal() {
        return ioModule.getTerminal();
    }

    @Override
    public BlockType type() {
        return BlockType.STORAGE;
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
