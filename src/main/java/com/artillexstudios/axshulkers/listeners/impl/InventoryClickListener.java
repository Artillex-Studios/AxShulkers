package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.MessageUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;
import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {

        boolean isShulkerGui = false;
        for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
            if (shulkerbox.getShulkerInventory().equals(event.getWhoClicked().getOpenInventory().getTopInventory())) isShulkerGui = true;
        }

        if (!isShulkerGui) return;

        if (!event.getWhoClicked().hasPermission("axshulkers.modify")) {
            event.setCancelled(true);
            return;
        }

        final ItemStack it = event.getClick() == ClickType.NUMBER_KEY ? event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem();

        if (!event.getWhoClicked().getOpenInventory().getTopInventory().equals(event.getClickedInventory())) {
            for (String s : CONFIG.getSection("blacklisted-items").getRoutesAsStrings(false)) {
                final Material mt = Material.getMaterial(CONFIG.getString("blacklisted-items." + s + ".material"));
                if (it == null || mt != null && !mt.equals(it.getType())) continue;

                if (CONFIG.getString("blacklisted-items." + s + ".name-contains") != null) {
                    if (it.getItemMeta() == null) continue;
                    if (it.getItemMeta().getDisplayName() == null) continue;

                    if (!it.getItemMeta().getDisplayName().contains(CONFIG.getString("blacklisted-items." + s + ".name-contains"))) continue;
                    event.setCancelled(true);
                }

                event.setCancelled(true);
                MessageUtils.sendMsgP(event.getWhoClicked(), "errors.banned-item");
                return;
            }
        }

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

        if (!event.getWhoClicked().hasPermission("axshulkers.modify")) {
            event.setCancelled(true);
            return;
        }

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
            ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.valueOf(MESSAGES.getString("close.sound")), 1f, 1f);
        }

        ShulkerUtils.setShulkerContents(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer().getOpenInventory().getTopInventory(), false);
    }
}
