package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.MessageUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;

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

        MessageUtils.sendMsgP(event.getPlayer(), "close.message");

        if (!MESSAGES.getString("close.sound").isEmpty()) {
            final Audience audience = Audience.audience((Audience) event.getPlayer());
            final Sound sound = Sound.sound(Key.key(MESSAGES.getString("close.sound")), Sound.Source.MASTER, 1f, 1f);
            audience.playSound(sound);
        }

        ShulkerUtils.setShulkerContents(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer().getOpenInventory().getTopInventory(), false);
    }
}
