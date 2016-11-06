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
import org.devathon.contest2016.block.BlockManager;
import org.devathon.contest2016.block.BlockType;
import org.devathon.contest2016.block.MachineBlock;
import org.devathon.contest2016.block.SerializeableLocation;
import org.devathon.contest2016.builder.Builder;
import org.devathon.contest2016.builder.impl.ItemBuilder;

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

        e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "§aSuccessfully connected!");

    }

    @Override
    public void interact(PlayerInteractEvent e) {

        if (DevathonPlugin.helper().equals(e.getItem(), DevathonPlugin.CRYSTAL_NAME, Material.DIAMOND)) {

            if (power >= 5) {
                e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "§cThis Energy Collector reached the max level!");
                e.setCancelled(true);
                return;
            }

            e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "§aUpgraded Energy Collector to level §b" + (++power));

            final ItemStack crystals = e.getPlayer().getInventory().getItemInMainHand();

            crystals.setAmount(crystals.getAmount() - 1);
            if (crystals.getAmount() < 1) crystals.setType(Material.AIR);

            e.getPlayer().getInventory().setItemInMainHand(crystals);
        } else
            e.setCancelled(true);

    }

    @Override
    public void breakBlock(BlockBreakEvent e) {
        if (terminal != null) terminal.getCollectors().remove(this);
        if (terminal != null) terminal.updateCounters();

        e.setCancelled(true);
        e.getBlock().setType(Material.AIR);

        final Location loc = e.getBlock().getLocation().clone();
        DevathonPlugin.helper().mid(loc);

        if (power > 1)
            e.getBlock().getWorld().dropItemNaturally(loc,
                    Builder.of(ItemBuilder.class).item(Material.DIAMOND, power - 1).name(DevathonPlugin.CRYSTAL_NAME).glow().build());

        e.getBlock().getWorld().dropItemNaturally(loc, Builder.of(ItemBuilder.class).item(Material.SEA_LANTERN).name(ITEM_NAME).build());


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

    public Block getBlock() {
        return location.getBlock();
    }

    public int getPower() {
        return power;
    }
}
