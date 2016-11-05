package org.devathon.contest2016.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.devathon.contest2016.DevathonPlugin;
import org.devathon.contest2016.block.impl.EnergyCollectorBlock;
import org.devathon.contest2016.block.impl.IOModuleBlock;
import org.devathon.contest2016.block.impl.StorageBlock;
import org.devathon.contest2016.block.impl.TerminalBlock;
import org.devathon.contest2016.util.Reflection;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block
 */
public enum BlockType {

    STORAGE(StorageBlock.class) {
        @Override
        public boolean is(ItemStack itemInHand) {
            return itemInHand.getType() == Material.ENDER_CHEST && itemInHand != null
                    && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()
                    && itemInHand.getItemMeta().getDisplayName().equals(StorageBlock.ITEM_NAME);
        }
    },
    IO_MODULE(IOModuleBlock.class) {
        @Override
        public boolean is(ItemStack itemInHand) {
            return itemInHand.getType() == Material.JUKEBOX && itemInHand != null
                    && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()
                    && itemInHand.getItemMeta().getDisplayName().equals(IOModuleBlock.ITEM_NAME);
        }
    },
    ENERGIE_COLLECTOR(EnergyCollectorBlock.class) {
        @Override
        public boolean is(ItemStack itemInHand) {
            return itemInHand.getType() == Material.SEA_LANTERN && itemInHand != null
                    && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()
                    && itemInHand.getItemMeta().getDisplayName().equals(EnergyCollectorBlock.ITEM_NAME);
        }
    },
    TERMINAL(TerminalBlock.class) {
        @Override
        public boolean is(ItemStack itemInHand) {
            return itemInHand.getType() == Material.WORKBENCH && itemInHand != null
                    && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()
                    && itemInHand.getItemMeta().getDisplayName().equals(TerminalBlock.ITEM_NAME);
        }
    };

    private final Class<?> clazz;

    BlockType(Class<?> c) {
        this.clazz = c;
    }

    public static BlockType of(Block block) {
        if (block == null || !block.hasMetadata("$blockType")) return null;
        final String type = block.getMetadata("$blockType").get(0).asString();
        return valueOf(type);
    }

    public static BlockType of(ItemStack item) {
        if (item == null) return null;

        for (BlockType type : values())
            if (type.is(item)) return type;

        return null;
    }



    public Object newInstance() {
        final Reflection reflection = DevathonPlugin.helper().reflection();
        return reflection.newInstance(reflection.getConstructor(clazz));
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public abstract boolean is(ItemStack itemInHand);

}
