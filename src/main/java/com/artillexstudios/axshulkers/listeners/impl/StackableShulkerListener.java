package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.utils.StackableShulkerSupport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

public class StackableShulkerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        StackableShulkerSupport.scanPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOpen(@NotNull InventoryOpenEvent event) {
        if (CONFIG.getBoolean("stacked-shulker-support.scan-opened-inventories", true)) {
            StackableShulkerSupport.scanInventory(event.getInventory());
        }

        if (event.getPlayer() instanceof Player player) {
            StackableShulkerSupport.scanPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(@NotNull InventoryClickEvent event) {
        // Wait until the click has been applied so moved shulkers are updated in their final slots.
        AxShulkers.getScheduler().runNextTick(task -> {
            if (event.getClickedInventory() != null) {
                StackableShulkerSupport.scanInventory(event.getClickedInventory());
            }
            StackableShulkerSupport.scanInventory(event.getView().getTopInventory());
            StackableShulkerSupport.scanInventory(event.getView().getBottomInventory());

            if (event.getWhoClicked() instanceof Player player) {
                StackableShulkerSupport.updateItem(player.getItemOnCursor());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDrag(@NotNull InventoryDragEvent event) {
        // Drag events may touch several slots, so rescan the visible inventories after Bukkit applies the drag.
        AxShulkers.getScheduler().runNextTick(task -> {
            StackableShulkerSupport.scanInventory(event.getView().getTopInventory());
            StackableShulkerSupport.scanInventory(event.getView().getBottomInventory());

            if (event.getWhoClicked() instanceof Player player) {
                StackableShulkerSupport.updateItem(player.getItemOnCursor());
            }
        });
    }
}
