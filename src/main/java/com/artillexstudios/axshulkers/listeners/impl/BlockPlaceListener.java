package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

public class BlockPlaceListener implements Listener {

    @EventHandler (ignoreCancelled = true)
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

        final List<HumanEntity> viewers = new ArrayList<>(shulkerbox.getShulkerInventory().getViewers());
        final Iterator<HumanEntity> viewerIterator = viewers.iterator();

        while (viewerIterator.hasNext()) {
            viewerIterator.next().closeInventory();
            viewerIterator.remove();
        }

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
