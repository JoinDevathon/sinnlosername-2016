package org.devathon.contest2016.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block
 */
public interface MachineBlock extends Serializable {

    void load(Location location);
    BlockType type();

    boolean is(ItemStack itemInHand);

    boolean is(Block block);

    void interact(PlayerInteractEvent e);

    void place(BlockPlaceEvent e);

    void breakBlock(BlockBreakEvent e);

}
