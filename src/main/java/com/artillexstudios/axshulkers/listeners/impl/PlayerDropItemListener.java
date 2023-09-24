package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerDropItemListener implements Listener {

    @EventHandler (ignoreCancelled = true)
    public void onDrop(@NotNull PlayerDropItemEvent event) {
        final ItemStack it = event.getItemDrop().getItemStack();
        if (!ShulkerUtils.isShulker(it)) return;

        final String name = ShulkerUtils.getShulkerName(it);
        final Shulkerbox shulkerbox = Shulkerboxes.getShulker(it, name);
        if (shulkerbox == null) return;

        ShulkerUtils.setShulkerContents(it, shulkerbox.getShulkerInventory(), false);
    }

}
