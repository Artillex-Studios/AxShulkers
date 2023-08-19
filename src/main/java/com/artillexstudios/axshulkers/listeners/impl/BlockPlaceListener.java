package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockPlaceListener implements Listener {

    @EventHandler (ignoreCancelled = true)
    public void onPlace(@NotNull BlockPlaceEvent event) {
        if (!ShulkerUtils.isShulker(event.getItemInHand())) return;

        final ItemStack it = event.getItemInHand().clone();

        Bukkit.getScheduler().runTask(AxShulkers.getInstance(), () -> {
            final Shulkerbox shulkerbox = Shulkerboxes.getShulker(it);
            if (shulkerbox == null) return;

            for (HumanEntity humanEntity : shulkerbox.getShulkerInventory().getViewers()) {
                humanEntity.closeInventory();
            }

            ShulkerUtils.setShulkerContents(event.getBlockPlaced(), shulkerbox.getShulkerInventory());

            AxShulkers.getDatabaseQueue().submit(() -> {
                AxShulkers.getDB().removeShulker(shulkerbox.getUUID());
            });
        });
    }
}
