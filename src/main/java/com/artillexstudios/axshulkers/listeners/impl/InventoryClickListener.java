package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.MessageUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;
import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;

public class InventoryClickListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onClick(@NotNull InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();

        final ItemStack it = event.getClick() == ClickType.NUMBER_KEY ? player.getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem();

        if (player.getOpenInventory().getTopInventory().getType().equals(InventoryType.SHULKER_BOX) && (event.getClick() == ClickType.NUMBER_KEY || !player.getOpenInventory().getTopInventory().equals(event.getClickedInventory()))) {
            for (String s : CONFIG.getSection("blacklisted-items").getRoutesAsStrings(false)) {
                if (it == null) continue;
                boolean banned = false;

                if (ShulkerUtils.isShulker(it)) banned = true;

                if (CONFIG.getString("blacklisted-items." + s + ".material") != null) {
                    if (!it.getType().toString().equalsIgnoreCase(CONFIG.getString("blacklisted-items." + s + ".material"))) continue;
                    banned = true;
                }

                if (CONFIG.getString("blacklisted-items." + s + ".name-contains") != null) {
                    if (it.getItemMeta() == null) continue;
                    if (!it.getItemMeta().getDisplayName().contains(CONFIG.getString("blacklisted-items." + s + ".name-contains"))) continue;
                    banned = true;
                }

                if (banned) {
                    event.setCancelled(true);
                    MessageUtils.sendMsgP(player, "errors.banned-item");
                    return;
                }
            }
        }

        final Shulkerbox shulker = ShulkerUtils.hasShulkerOpen(player);
        if (shulker == null) return;

        if (!player.hasPermission("axshulkers.modify")) {
            event.setCancelled(true);
            return;
        }

        if (ShulkerUtils.isShulker(it)) event.setCancelled(true);
        if (ShulkerUtils.isShulker(event.getCurrentItem())) event.setCancelled(true);
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

        MessageUtils.sendMsgP(event.getPlayer(), "close.message");

        if (!MESSAGES.getString("close.sound").isEmpty()) {
            ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.valueOf(MESSAGES.getString("close.sound")), 1f, 1f);
        }

        ShulkerUtils.setShulkerContents(shulker.getItem(), event.getPlayer().getOpenInventory().getTopInventory(), false);

        if (!CONFIG.getBoolean("auto-clear-shulkers", false) && !(CONFIG.getBoolean("auto-clear-in-creative", true) && event.getPlayer().getGameMode().equals(GameMode.CREATIVE))) return;

        // don't clear the shulker if it has changed
        if (!shulker.getReference().get().equals(shulker.getItem())) return;
        ShulkerUtils.removeShulkerUUID(shulker.getItem());
        Shulkerboxes.removeShulkerbox(shulker.getUUID());
        AxShulkers.getDB().removeShulker(shulker.getUUID());
    }
}
