package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractListener implements Listener {

    @EventHandler (ignoreCancelled = true)
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) return;
        Shulkerbox shulkerbox;
        if ((shulkerbox = ShulkerUtils.hasShulkerOpen(event.getPlayer())) == null) return;
        shulkerbox.close();
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        Shulkerbox shulkerbox;
        if ((shulkerbox = ShulkerUtils.hasShulkerOpen(event.getPlayer())) == null) return;
        shulkerbox.close();
    }
}
