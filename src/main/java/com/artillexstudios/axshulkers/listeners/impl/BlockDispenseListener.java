package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

public class BlockDispenseListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onDispense(@NotNull BlockDispenseEvent event) {
        if (!ShulkerUtils.isShulker(event.getItem())) return;

        if (CONFIG.getBoolean("disable-shulker-dispensing")) {
            event.setCancelled(true);
            return;
        }

        if (event.getItem().getAmount() != 1) {
            event.getItem().setAmount(1);
            event.setCancelled(true);
            return;
        }

        if (event.getBlock().getType().equals(Material.DROPPER))
            handleDropper(event);
        else
            handleDispenser(event);
    }

    public void handleDropper(@NotNull BlockDispenseEvent event) {
        final ItemStack it = event.getItem();

        final String name = ShulkerUtils.getShulkerName(it);
        final Shulkerbox shulkerbox = Shulkerboxes.getShulker(it, name);
        if (shulkerbox == null) return;

        ShulkerUtils.setShulkerContents(event.getItem(), shulkerbox.getShulkerInventory(), false);
    }

    public void handleDispenser(@NotNull BlockDispenseEvent event) {

        if (ShulkerUtils.getShulkerUUID(event.getItem()) == null) return;

        final Directional directional = (Directional) event.getBlock().getBlockData();
        final Block facingBlock = event.getBlock().getRelative(directional.getFacing());
        if (!facingBlock.getType().equals(Material.AIR)) return;

        final Dispenser dispenser = (Dispenser) event.getBlock().getState();

        event.setCancelled(true);

        for (ItemStack it : dispenser.getInventory()) {
            if (!ShulkerUtils.isShulker(it)) continue;

            final String name = ShulkerUtils.getShulkerName(it);
            final Shulkerbox shulkerbox = Shulkerboxes.getShulker(it, name);
            if (shulkerbox == null) continue;

            shulkerbox.close();

            ShulkerUtils.setShulkerContents(it, shulkerbox.getShulkerInventory(), true);
            ShulkerUtils.removeShulkerUUID(it);

            AxShulkers.getDatabaseQueue().submit(() -> {
                AxShulkers.getDB().removeShulker(shulkerbox.getUUID());
                Shulkerboxes.removeShulkerbox(shulkerbox.getUUID());
            });
        }

        dispenser.dispense();
    }
}
