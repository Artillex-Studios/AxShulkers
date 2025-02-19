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
        ItemStack it = event.getItemDrop().getItemStack();
        if (!ShulkerUtils.isShulker(it)) return;

        String name = ShulkerUtils.getShulkerName(it);
        Shulkerbox shulkerbox = Shulkerboxes.getShulker(it, name);
        if (shulkerbox == null) return;
        shulkerbox.close();

        it = shulkerbox.getItem();

        if (it.getAmount() != 1) {
            it.setAmount(1);
            event.setCancelled(true);
        }

        ShulkerUtils.setShulkerContents(it, shulkerbox.getShulkerInventory(), false);
        event.getItemDrop().setItemStack(it);
    }

}
