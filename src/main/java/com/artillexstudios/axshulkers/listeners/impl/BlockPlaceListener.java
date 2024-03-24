package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

public class BlockPlaceListener implements Listener {

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlace(@NotNull BlockPlaceEvent event) {
        if (!ShulkerUtils.isShulker(event.getItemInHand())) return;

        if (CONFIG.getBoolean("disable-shulker-placing")) {
            event.setCancelled(true);
            return;
        }

        if (event.getItemInHand().getAmount() != 1) {
            event.setCancelled(true);
            event.getItemInHand().setAmount(1);
            return;
        }

        final ItemStack it = event.getItemInHand().clone();

        final String name = ShulkerUtils.getShulkerName(it);

        final Shulkerbox shulkerbox = Shulkerboxes.getShulker(it, name);
        if (shulkerbox == null) return;

        shulkerbox.close();

        ShulkerUtils.clearShulkerContents(event.getBlockPlaced());

        AxShulkers.getDatabaseQueue().submit(() -> {
            AxShulkers.getDB().removeShulker(shulkerbox.getUUID());
            Shulkerboxes.removeShulkerbox(shulkerbox.getUUID());
        });

        AxShulkers.getFoliaLib().getImpl().runAtLocation(event.getBlockPlaced().getLocation(), () -> {
            ShulkerUtils.setShulkerContents(event.getBlockPlaced(), shulkerbox.getShulkerInventory());
        });
    }

}
