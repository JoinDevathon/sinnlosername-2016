package org.devathon.contest2016.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.devathon.contest2016.block.BlockType;
import org.devathon.contest2016.block.impl.IOModuleBlock;
import org.devathon.contest2016.builder.Builder;
import org.devathon.contest2016.builder.impl.ItemBuilder;

/**
 * Created by Florian on 06.11.16 in org.devathon.contest2016.listener
 */
public class MiscListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_AIR) return;
        if (e.getHand() != EquipmentSlot.HAND) return;

        final BlockType type = BlockType.of(e.getItem());

        if (type == null || type != BlockType.IO_MODULE) return;

        short frequency = Short.parseShort(e.getItem().getItemMeta().getLore().get(0).substring(15).trim());

        frequency = e.getAction() == Action.RIGHT_CLICK_AIR ? ++frequency : --frequency;

        final ItemStack newStack = Builder.of(ItemBuilder.class)
                .item(e.getItem()).name(IOModuleBlock.ITEM_NAME).lore("§aFrequency:§7 0").build();

        e.getPlayer().getInventory().setItemInMainHand(newStack);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getInventory() == null || e.getCurrentItem() == null) return;

        // Preventing people from cheating c:
        if (e.getInventory().getType() == InventoryType.ANVIL || e.getInventory().getType() == InventoryType.CRAFTING) {
            final BlockType type = BlockType.of(e.getCurrentItem());

            if (type != null)
                e.setCancelled(true);

        }
    }


}
