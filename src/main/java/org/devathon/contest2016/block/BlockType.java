package org.devathon.contest2016.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.devathon.contest2016.DevathonPlugin;
import org.devathon.contest2016.block.impl.EnergyCollectorBlock;
import org.devathon.contest2016.util.Reflection;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block
 */
public enum BlockType {

    STORAGE(null) {
        @Override
        public boolean is(ItemStack itemInHand) {
            return false;
        }
    },
    PIPE(null) {
        @Override
        public boolean is(ItemStack itemInHand) {
            return false;
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
    TERMINAL(null) {
        @Override
        public boolean is(ItemStack itemInHand) {
            return false;
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

    public Object newInstance() {
        final Reflection reflection = DevathonPlugin.helper().reflection();
        return reflection.newInstance(reflection.getConstructor(clazz));
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public abstract boolean is(ItemStack itemInHand);

}
