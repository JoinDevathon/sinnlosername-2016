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
public class EnergyCollectorBlock implements MachineBlock {

    public static final String ITEM_NAME = "§6§lEnergy Collector";

    private int power = 1;
    private transient Location location;
    private transient TerminalBlock terminal;

    @Override
    public void load(Location location) {
        this.location = location;
        location.getBlock().setMetadata("$blockType", new FixedMetadataValue(DevathonPlugin.helper().plugin(), type().name()));

    }

    @Override
    public void place(BlockPlaceEvent e) {

        final BlockType type = BlockType.of(e.getBlockAgainst());

        if (type == null || type != BlockType.TERMINAL) {

            BlockManager.getInstance().remove(e.getBlock().getLocation());
            e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "§cYou can only place an Energy Collector against a Terminal!");
            e.setCancelled(true);

            return;
        }

        load(e.getBlock().getLocation());

        terminal = (TerminalBlock) BlockManager.getInstance().getBlocks()
                .get(new SerializeableLocation(e.getBlockAgainst().getLocation()));

        terminal.getCollectors().add(this);
        terminal.updateCounters();

        e.getPlayer().sendMessage("You placed an energy collector! :o");
    }

    @Override
    public void interact(PlayerInteractEvent e) {
        e.setCancelled(true);
        e.getPlayer().sendMessage("You interacted with an energy collector");
    }

    @Override
    public void breakBlock(BlockBreakEvent e) {
        terminal.getCollectors().remove(this);
        terminal.updateCounters();
        e.getPlayer().sendMessage("Your broke an energy collector");
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
        return BlockType.ENERGIE_COLLECTOR;
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

    public int getPower() {
        return power;
    }
}
