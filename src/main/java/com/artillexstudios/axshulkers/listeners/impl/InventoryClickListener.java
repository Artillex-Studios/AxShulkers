package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.BlacklistUtils;
import com.artillexstudios.axshulkers.utils.MessageUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;
import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;

public class InventoryClickListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onClick(@NotNull InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (player.getOpenInventory().getTopInventory().getType() != InventoryType.SHULKER_BOX) return;

        ItemStack it = getItem(event);

        if (!player.hasPermission("axshulkers.modify")) {
            event.setCancelled(true);
            return;
        }

        if (BlacklistUtils.isBlacklisted(it)) {
            event.setCancelled(true);
            MessageUtils.sendMsgP(player, "errors.banned-item");
            return;
        }

        if (ShulkerUtils.isShulker(it)) event.setCancelled(true);
        if (ShulkerUtils.isShulker(event.getCurrentItem())) event.setCancelled(true);
    }

    @Nullable
    private ItemStack getItem(InventoryClickEvent event) {
        if (event.getClick() == ClickType.NUMBER_KEY && event.getClickedInventory() != null) {
            Inventory inventory;
            if (event.getClickedInventory().getType() == InventoryType.PLAYER && event.getAction() != InventoryAction.HOTBAR_SWAP)
                inventory = event.getView().getTopInventory();
            else
                inventory = event.getView().getBottomInventory();
            return inventory.getItem(event.getHotbarButton());
        }
        return event.getCurrentItem();
    }

    @EventHandler
    public void onDrag(@NotNull InventoryDragEvent event) {
        final Shulkerbox shulker = ShulkerUtils.hasShulkerOpen((Player) event.getWhoClicked());
        if (shulker == null) return;

        if (!event.getWhoClicked().hasPermission("axshulkers.modify")) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onClose(@NotNull InventoryCloseEvent event) {
        final Shulkerbox shulker = ShulkerUtils.hasShulkerOpen((Player) event.getPlayer());
        if (shulker == null) return;

        MessageUtils.sendMsgP(event.getPlayer(), "close.message", Collections.singletonMap("%name%", shulker.getTitle()));

        if (!MESSAGES.getString("close.sound").isEmpty()) {
            ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.valueOf(MESSAGES.getString("close.sound")), 1f, 1f);
        }

        ShulkerUtils.setShulkerContents(shulker.getItem(), event.getPlayer().getOpenInventory().getTopInventory(), false);

        if (CONFIG.getBoolean("enable-obfuscation", false) || (!CONFIG.getBoolean("auto-clear-shulkers", false) && !(CONFIG.getBoolean("auto-clear-in-creative", true) && event.getPlayer().getGameMode().equals(GameMode.CREATIVE)))) return;

        // don't clear the shulker if it has changed
        if (!shulker.getReference().get().equals(shulker.getItem())) return;
        ShulkerUtils.removeShulkerUUID(shulker.getItem());
        Shulkerboxes.removeShulkerbox(shulker.getUUID());
        AxShulkers.getDB().removeShulker(shulker.getUUID());
    }
}
