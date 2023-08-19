package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {

        boolean isShulkerGui = false;
        for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
            if (shulkerbox.getShulkerInventory().equals(event.getWhoClicked().getOpenInventory().getTopInventory())) isShulkerGui = true;
        }

        if (!isShulkerGui) return;

        final ItemStack it = event.getClick() == ClickType.NUMBER_KEY ? event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem();
        if (ShulkerUtils.isShulker(it)) event.setCancelled(true);
        if (ShulkerUtils.isShulker(event.getCurrentItem())) event.setCancelled(true);

        Bukkit.getScheduler().runTask(AxShulkers.getInstance(), () -> {
            ShulkerUtils.setShulkerContents(event.getWhoClicked().getInventory().getItemInMainHand(), event.getWhoClicked().getOpenInventory().getTopInventory(), false);
        });
    }

    @EventHandler
    public void onDrag(@NotNull InventoryDragEvent event) {

        boolean isShulkerGui = false;
        for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
            if (shulkerbox.getShulkerInventory().equals(event.getWhoClicked().getOpenInventory().getTopInventory())) isShulkerGui = true;
        }

        if (!isShulkerGui) return;

        Bukkit.getScheduler().runTask(AxShulkers.getInstance(), () -> {
            ShulkerUtils.setShulkerContents(event.getWhoClicked().getInventory().getItemInMainHand(), event.getWhoClicked().getOpenInventory().getTopInventory(), false);
        });
    }

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {

        boolean isShulkerGui = false;
        for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
            if (shulkerbox.getShulkerInventory().equals(event.getPlayer().getOpenInventory().getTopInventory())) isShulkerGui = true;
        }

        if (!isShulkerGui) return;

        ShulkerUtils.setShulkerContents(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer().getOpenInventory().getTopInventory(), false);
    }
}
