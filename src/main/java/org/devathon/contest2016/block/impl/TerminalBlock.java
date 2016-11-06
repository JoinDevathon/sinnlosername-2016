package org.devathon.contest2016.block.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.devathon.contest2016.DevathonPlugin;
import org.devathon.contest2016.block.BlockType;
import org.devathon.contest2016.block.MachineBlock;
import org.devathon.contest2016.builder.Builder;
import org.devathon.contest2016.builder.impl.ItemBuilder;
import org.devathon.contest2016.builder.impl.ThreadBuilder;
import org.devathon.contest2016.inventory.ClickAction;
import org.devathon.contest2016.inventory.InventoryMenu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block.impl
 */
public class TerminalBlock implements MachineBlock {

    public static final String ITEM_NAME = "§6§lTerminal";
    private final Set<EnergyCollectorBlock> collectors = new HashSet<>();
    private final Set<IOModuleBlock> ioModules = new HashSet<>();

    private int energy = 0;

    private transient boolean changedItems;
    private transient List<ItemStack> items;
    private transient BukkitTask task;
    private transient int lastEnergy;
    private transient InventoryMenu menu;
    private transient Location location;

    @Override
    public void load(Location location) {

        //transient items doesn't initialize, don't ask me why.
        this.location = location;
        this.items = new ArrayList<>();
        this.lastEnergy = -1;
        this.changedItems = false;


        location.getBlock().setMetadata("$blockType", new FixedMetadataValue(DevathonPlugin.helper().plugin(), type().name()));

        collectors.forEach(c -> c.setTerminal(this));
        ioModules.forEach(c -> c.setTerminal(this));


        final ItemStack greyGlass =
                Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, (short) 7).name(" ").build();

        menu = new InventoryMenu(45, "§6§lTerminal");
        menu.setSame(greyGlass, ClickAction.CANCEL, 0, 1, 2, 6, 7, 8);

        setupDefaultAction();
        updateCounters();
        updateEnergy();
        startThread();


    }

    public void insertItem(ItemStack stack) {
        if (stack == null) return;

        if (alreadyIn(stack)) {

            Bukkit.broadcastMessage("Already in: " + items.size());

            for (ItemStack item : items) {

                if (!equalsNoAmount(item, stack)) continue;
                if (item.getAmount() >= item.getMaxStackSize()) continue;

                while (item.getAmount() < item.getMaxStackSize() && stack.getAmount() > 0) {

                    item.setAmount(item.getAmount() + 1);
                    stack.setAmount(stack.getAmount() - 1);

                }

                if (stack.getAmount() < 1) break;

            }

            if (stack.getAmount() > 0)
                items.add(stack);

        } else {
            items.add(stack);
            Bukkit.broadcastMessage("Not in, adding. " + items.size());
        }

        changedItems = true;

    }

    public void removeItem(ItemStack stack, int slot) {
        final ItemStack item = items.get(slot);

        if (item.getAmount() == stack.getAmount())
            items.remove(slot);
        else
            item.setAmount(item.getAmount() - stack.getAmount());

        changedItems = true;
    }


    public boolean alreadyIn(ItemStack stack) {
        if (stack == null) throw new NullPointerException("Stack is null, dafuq?");
        if (items == null) throw new NullPointerException("Items is null, dafuq?");

        for (ItemStack item : items)
            if (item != null && equalsNoAmount(item, stack))
                return true;
        return false;
    }

    public boolean equalsNoAmount(ItemStack i1, ItemStack i2) {
        final int orig = i1.getAmount();
        i1.setAmount(i2.getAmount());
        final boolean equal = i1.equals(i2);
        i1.setAmount(orig);
        return equal;
    }

    @Override
    public void serialize() {

    }

    public void setupDefaultAction() {

        menu.setClickOtherAction(e -> {
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
                e.setCancelled(true);
        });

        menu.setDefaultAction(e -> {

            e.getWhoClicked().sendMessage("T R I G G E R E D");

            if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
                if (e.getCurrentItem() == null) {
                    e.setCancelled(true);
                    return;
                }

                final InventoryAction action = e.getAction();

                ItemStack toRemove;
                switch (action) {
                    case MOVE_TO_OTHER_INVENTORY:
                    case PICKUP_ALL:
                        toRemove = e.getCurrentItem();
                        break;
                    case PICKUP_HALF:
                        toRemove = e.getCurrentItem().clone();
                        toRemove.setAmount((toRemove.getAmount() / 2) + 1);
                        break;
                    default:
                        e.setCancelled(true);
                        return;
                }

                removeItem(toRemove, DevathonPlugin.helper().redirecter().reverseRedirect(e.getSlot()) - 1);

                e.getWhoClicked().sendMessage("Took: " + e.getCurrentItem().getType() + ":" + e.getCurrentItem().getAmount());
                e.getWhoClicked().sendMessage("action: " + action.name());

                return;
            }

            e.setCancelled(true);

            if (e.isRightClick() || e.isShiftClick()) return;

            e.getWhoClicked().sendMessage("Inserting " + e.getCursor().getType());
            insertItem(e.getCursor());
            e.setCursor(new ItemStack(Material.AIR));


        });
    }

    public void startThread() {
        task = Builder.of(ThreadBuilder.class).with(() -> {

            collectors.forEach(c -> energy += Math.pow(20, c.getPower()));

            // storages take energy
            ioModules.forEach(m -> energy -= 50 * m.getStorages().size());

            // ioModules take many energy
            energy -= Math.pow(15, ioModules.size());

            //terminal itself take energy
            energy -= 10;

            energy = energy < 0 ? 0 : energy;

            // max energy: 1.000.000.000
            energy = energy > 1000000000 ? 1000000000 : energy;

            if (energy != lastEnergy) updateEnergy();
            lastEnergy = energy;


            if (!changedItems) {
                menu.update();
                return;
            }

            changedItems = false;

            int i = 0;
            while (i < items.size() && i < 29) {
                final ItemStack stack = items.get(i);

                menu.set(stack, DevathonPlugin.helper().redirecter().redirect(i + 1), menu.getDefaultAction());

                i++;
            }

            while (++i < 29)
                menu.remove(DevathonPlugin.helper().redirecter().redirect(i));

            menu.update();

        }).start(false, 20).build();

    }

    public void updateCounters() {

        // ioModule count
        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.JUKEBOX, ioModules.size()).name(IOModuleBlock.ITEM_NAME)
                .lore("§aConntected: §7" + ioModules.size()).build(), 3, ClickAction.CANCEL);

        // collector count
        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.SEA_LANTERN, collectors.size()).name(EnergyCollectorBlock.ITEM_NAME)
                .lore("§aConntected: §7" + collectors.size()).build(), 4, ClickAction.CANCEL);

        // storage count
        int i = 0;
        for (IOModuleBlock ioModule : ioModules)
            i += ioModule.getStorages().size();

        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.ENDER_CHEST, i).name(StorageBlock.ITEM_NAME)
                .lore("§aConntected: §7" + i).build(), 5, ClickAction.CANCEL);

        menu.update();
    }

    public void updateEnergy() {

        short glassType = 7;
        int height = 0;

        if (energy > 10000000) {
            height = 4;
            glassType = 5;
        } else if (energy > 50000) {
            height = 3;
            glassType = 4;
        } else if (energy > 5000) {
            height = 2;
            glassType = 4;
        } else if (energy > 0) {
            height = 1;
            glassType = 14;
        }

        int hc = 0;

        while (hc <= height) {
            menu.set(Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, glassType)
                    .name("§a§lEnergy: §7" + energy).build(), 36 - (hc * 9), ClickAction.CANCEL);

            menu.set(Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, glassType)
                    .name("§a§lEnergy: §7" + energy).build(), 44 - (hc * 9), ClickAction.CANCEL);

            hc++;
        }

        while (hc < 4) {
            menu.set(Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, (short) 7)
                    .name("§a§lEnergy: §7" + energy).build(), 36 - (hc * 9), ClickAction.CANCEL);

            menu.set(Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, (short) 7)
                    .name("§a§lEnergy: §7" + energy).build(), 44 - (hc * 9), ClickAction.CANCEL);

            hc++;
        }

        menu.update();


    }

    @Override
    public void place(BlockPlaceEvent e) {
        load(e.getBlock().getLocation());
        e.getPlayer().sendMessage("You placed a terminal");
    }

    @Override
    public void interact(PlayerInteractEvent e) {
        if (!e.getPlayer().isSneaking()) {
            e.setCancelled(true);

            menu.open(e.getPlayer());

        }

        e.getPlayer().sendMessage("You interacted with a terminal");
        e.getPlayer().sendMessage("This terminal is connected with " + collectors.size() + " collectors");
        e.getPlayer().sendMessage("This terminal is connected with " + ioModules.size() + " IOModules");

    }

    @Override
    public void breakBlock(BlockBreakEvent e) {
        if (task != null) task.cancel();
        e.getPlayer().sendMessage("You broke a terminal");
    }

    @Override
    public TerminalBlock getTerminal() {
        return this;
    }

    @Override
    public BlockType type() {
        return BlockType.TERMINAL;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TerminalBlock that = (TerminalBlock) o;

        return location != null ? location.equals(that.location) : that.location == null;

    }

    @Override
    public int hashCode() {
        return location != null ? location.hashCode() : 0;
    }

    public Set<EnergyCollectorBlock> getCollectors() {
        return collectors;
    }

    public Set<IOModuleBlock> getIOModules() {
        return ioModules;
    }
}
