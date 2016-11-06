package org.devathon.contest2016.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.devathon.contest2016.block.BlockManager;
import org.devathon.contest2016.block.BlockType;
import org.devathon.contest2016.block.MachineBlock;
import org.devathon.contest2016.block.SerializeableLocation;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.listener
 */
public class GeneralListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        for (BlockType type : BlockType.values())
            if (type.is(e.getItemInHand())) {
                final MachineBlock block = BlockManager.getInstance()
                        .addBlock(e.getBlock().getLocation(), (MachineBlock) type.newInstance());

                block.place(e);


            }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        final MachineBlock block = BlockManager.getInstance()
                .getBlocks().get(new SerializeableLocation(e.getBlock().getLocation()));

        if (block == null) return;

        block.breakBlock(e);
        if (!e.isCancelled()) BlockManager.getInstance().remove(e.getBlock().getLocation());


    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null || e.getHand() != EquipmentSlot.HAND) return;

        BlockManager.getInstance().getBlocks().values().stream()
                .filter(b -> b.is(e.getClickedBlock())).forEach((b) -> b.interact(e));

    }

}
