package org.devathon.contest2016.block.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
import org.devathon.contest2016.util.Pair;

import java.util.*;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block.impl
 */
public class TerminalBlock implements MachineBlock {

    public static final String ITEM_NAME = "§6§lTerminal";
    private final Set<EnergyCollectorBlock> collectors = new HashSet<>();
    private final Set<IOModuleBlock> ioModules = new HashSet<>();

    private int energy = 0;
    private List<Pair<Map<String, Object>, Map<String, Object>>> serializedItems;

    private transient int scroll;
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
        this.changedItems = true;
        this.scroll = 0;
        if (this.serializedItems == null) this.serializedItems = new ArrayList<>();

        for (Pair<Map<String, Object>, Map<String, Object>> pair : serializedItems) {
            final ItemStack stack = ItemStack.deserialize(pair.getKey());

            if (pair.getValue() != null) {
                final ItemMeta meta =
                        (ItemMeta) ConfigurationSerialization.deserializeObject(
                                pair.getValue(), ConfigurationSerialization.getClassByAlias("ItemMeta"));
                stack.setItemMeta(meta);
            }

            items.add(stack);
        }

        serializedItems.clear();

        location.getBlock().setMetadata("$blockType", new FixedMetadataValue(DevathonPlugin.helper().plugin(), type().name()));

        collectors.forEach(c -> c.setTerminal(this));
        ioModules.forEach(c -> c.setTerminal(this));


        final ItemStack greyGlass =
                Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, (short) 7).name(" ").build();

        menu = new InventoryMenu(45, "§6§lTerminal");
        menu.setSame(greyGlass, ClickAction.CANCEL, 0, 1, 2, 6, 7, 8);

        setupDefaultAction();
        setScrollItems();
        updateCounters();
        updateEnergy();
        startThread();

        // for (int i = 0; i < 50; i++) {
        // insertItem(new ItemStack(Material.getMaterial(ThreadLocalRandom.current().nextInt(20, 30)), 64));
        // }

    }

    public void insertItem(ItemStack stack) {
        if (stack == null) return;

        if (alreadyIn(stack)) {

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

        } else
            items.add(stack);


        changedItems = true;

    }

    public boolean wouldBeFull(int i) {
        return (totalItemCount() + i) > maxStorage();
    }

    public int maxStorage() {
        int storages = 0;
        for (IOModuleBlock ioModule : ioModules)
            storages += ioModule.getStorages().size();

        return storages * 18 * 64;
    }

    public int totalItemCount() {
        int i = 0;
        for (ItemStack item : items) {
            if (item == null) continue;
            i += item.getAmount();
        }
        return i;
    }

    public void removeItem(ItemStack stack, int slot) {
        if (slot >= items.size()) return; // This should not happen, but because of shitty inventory bugs it does. -_-
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

        //noinspection Convert2streamapi

        for (ItemStack item : items) {
            final Map<String, Object> sItem = item.serialize();
            sItem.remove("meta");
            serializedItems.add(new Pair<>(sItem, item.hasItemMeta() ? item.getItemMeta().serialize() : null));
        }

    }

    public void setupDefaultAction() {

        menu.setClickOtherAction(e -> {
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
                e.setCancelled(true);
        });

        menu.setDefaultAction(e -> {


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

                return;
            }

            e.setCancelled(true);

            if (e.isRightClick() || e.isShiftClick()) return;

            if (wouldBeFull(e.getCursor().getAmount())) {
                e.getWhoClicked().sendMessage(DevathonPlugin.PREFIX + "§cNot enough space for this itemstack!");
                e.getWhoClicked().sendMessage(DevathonPlugin.PREFIX + "§cAdd more storage blocks to get more storage!");
                return;
            }

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

            if (energy < 1)
                menu.getViewers().forEach(player -> {
                    player.closeInventory();
                    player.sendMessage(DevathonPlugin.PREFIX + "§cTerminal is out of energy.");
                });
            else
                updateCounters();


            setScrollItems();

            if (!changedItems) {
                menu.update();
                return;
            }

            changedItems = false;

            int itemCursor = scroll * 7;
            int invCursor = 0;
            while (itemCursor < items.size() && invCursor < 28) {
                final ItemStack stack = items.get(itemCursor);

                menu.set(stack, DevathonPlugin.helper().redirecter().redirect(invCursor + 1), menu.getDefaultAction());

                itemCursor++;
                invCursor++;
            }

            while (invCursor < 28) {
                invCursor++;
                menu.remove(DevathonPlugin.helper().redirecter().redirect(invCursor));
            }

            menu.update();

        }).start(false, 20).build();

    }

    public void setScrollItems() {
        final ItemStack cantScroll =
                Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, (short) 3).name("§cCan't scroll anymore").build();

        final int maxDown = items.size() / 7;

        if (scroll + 1 > maxDown)
            menu.set(cantScroll, 7, ClickAction.CANCEL);
        else
            menu.set(Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, (short) 3).name("§9Scroll down").build(), 7, e -> {
                e.setCancelled(true);
                if (scroll + 1 <= maxDown) {
                    scroll += 1;
                    setScrollItems();
                    changedItems = true;
                }
            });

        if (scroll - 1 < 0)
            menu.set(cantScroll, 6, ClickAction.CANCEL);
        else
            menu.set(Builder.of(ItemBuilder.class).item(Material.STAINED_GLASS_PANE, 1, (short) 3).name("§9Scroll up").build(), 6, e -> {
                e.setCancelled(true);
                if (scroll - 1 >= 0) {
                    scroll -= 1;
                    setScrollItems();
                    changedItems = true;
                }
            });

    }

    public void updateCounters() {

        // ioModule count
        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.JUKEBOX, ioModules.size()).name(IOModuleBlock.ITEM_NAME)
                .lore("§aConntected: §7" + ioModules.size()).build(), 3, ClickAction.CANCEL);

        // energy count
        int energy = 0;
        for (EnergyCollectorBlock c : collectors)
            energy += Math.pow(20, c.getPower());

        // collector count
        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.SEA_LANTERN, collectors.size()).name(EnergyCollectorBlock.ITEM_NAME)
                .lore("§aConntected: §7" + collectors.size(), "§aEnergy/s: §7" + energy).build(), 4, ClickAction.CANCEL);

        // storage count
        int cCount = 0;
        for (IOModuleBlock ioModule : ioModules)
            cCount += ioModule.getStorages().size();

        menu.set(Builder.of(ItemBuilder.class)
                .item(Material.ENDER_CHEST, cCount).name(StorageBlock.ITEM_NAME)
                .lore("§aConntected: §7" + cCount, "§aMaxStorage: §7" + maxStorage()).build(), 5, ClickAction.CANCEL);

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
        e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "§aSuccessfully placed terminal!");
    }

    @Override
    public void interact(PlayerInteractEvent e) {
        if (!e.getPlayer().isSneaking()) {
            e.setCancelled(true);

            if (energy < 1) {
                e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "§cNot enough energy to open terminal!");
                e.getPlayer().sendMessage(DevathonPlugin.PREFIX +
                        "§cConnect more energy collectors or improve already connected energy collectors to open the terminal.");
                return;
            }

            menu.open(e.getPlayer());
        }

    }

    @Override
    public void breakBlock(BlockBreakEvent e) {
        if (task != null) task.cancel();

        if (totalItemCount() > 0) {
            e.getPlayer().sendMessage(DevathonPlugin.PREFIX + "§cYou can't break this terminal because it isn't empty!");
            e.setCancelled(true);
            return;
        }

        final Location l = e.getBlock().getLocation().clone();

        DevathonPlugin.helper().mid(l);

        e.setCancelled(true);
        e.getBlock().setType(Material.AIR);

        e.getBlock().getWorld().dropItemNaturally(l,
                Builder.of(ItemBuilder.class).item(Material.WORKBENCH).name(TerminalBlock.ITEM_NAME).build());

        ioModules.forEach(m -> m.breakBlock(new BlockBreakEvent(m.getBlock(), e.getPlayer())));
        ((Set<EnergyCollectorBlock>) ((HashSet<EnergyCollectorBlock>) collectors).clone())
                .forEach(c -> c.breakBlock(new BlockBreakEvent(c.getBlock(), e.getPlayer())));

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
