package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        final ItemStack it = event.getItem().clone();

        final String name = ShulkerUtils.getShulkerName(it);
        final Shulkerbox shulkerbox = Shulkerboxes.getShulker(it, name);
        if (shulkerbox == null) return;

        ShulkerUtils.setShulkerContents(event.getItem(), shulkerbox.getShulkerInventory(), false);
    }

    public void handleDispenser(@NotNull BlockDispenseEvent event) {
        final ItemStack it = event.getItem().clone();

        final Directional directional = (Directional) event.getBlock().getBlockData();
        final Block facingBlock = event.getBlock().getRelative(directional.getFacing());
        if (!facingBlock.getType().equals(Material.AIR)) return;

        AxShulkers.getFoliaLib().getImpl().runAtLocation(facingBlock.getLocation(), () -> {
            ShulkerUtils.clearShulkerContents(event.getBlock());

            final String name = ShulkerUtils.getShulkerName(it);
            final Shulkerbox shulkerbox = Shulkerboxes.getShulker(it, name);
            if (shulkerbox == null) return;

            final List<HumanEntity> viewers = new ArrayList<>(shulkerbox.getShulkerInventory().getViewers());
            final Iterator<HumanEntity> viewerIterator = viewers.iterator();

            while (viewerIterator.hasNext()) {
                viewerIterator.next().closeInventory();
                viewerIterator.remove();
            }

            ShulkerUtils.setShulkerContents(facingBlock, shulkerbox.getShulkerInventory());

            AxShulkers.getDatabaseQueue().submit(() -> {
                AxShulkers.getDB().removeShulker(shulkerbox.getUUID());
                Shulkerboxes.removeShulkerbox(shulkerbox.getUUID());
            });
        });
    }
}
