package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.MessageUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;
import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;

public class ShulkerOpenListener implements Listener {
    private final HashMap<Player, Long> cds = new HashMap<>();

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        final Player player = event.getPlayer();
        if (openShulker(player, player.getInventory().getItemInMainHand())) event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onShulkerClick(@NotNull InventoryClickEvent event) {
        if (!event.getClick().equals(ClickType.RIGHT)) return;
        if (!CONFIG.getBoolean("opening-from-inventory.enabled")) return;

        final Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() != null && !event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            if (!event.getClickedInventory().getType().equals(InventoryType.ENDER_CHEST)) return;
            if (!CONFIG.getBoolean("opening-from-inventory.open-from-enderchest")) return;
        }

        if (event.getView().getTopInventory().getType().equals(InventoryType.SHULKER_BOX)) {
            for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
                if (!shulkerbox.getShulkerInventory().equals(event.getView().getTopInventory())) continue;
                if (!shulkerbox.getUUID().equals(ShulkerUtils.getShulkerUUID(event.getCurrentItem()))) continue;

                event.setCancelled(true);
                Bukkit.getScheduler().runTask(AxShulkers.getInstance(), () -> {
                    event.getWhoClicked().closeInventory();
                });
                return;
            }
        }

        if (openShulker(player, event.getCurrentItem())) event.setCancelled(true);
    }

    private boolean openShulker(@NotNull Player player, @NotNull ItemStack it) {
        if (!ShulkerUtils.isShulker(it)) return false;
        if (CONFIG.getStringList("blacklisted-worlds").contains(player.getWorld().getName())) {
            MessageUtils.sendMsgP(player, "errors.blacklisted-world");
            return false;
        }

        if (cds.containsKey(player) && System.currentTimeMillis() - cds.get(player) < CONFIG.getLong("open-cooldown-miliseconds")) {
            MessageUtils.sendMsgP(player, "cooldown", Map.of("%seconds%", Long.toString((CONFIG.getLong("open-cooldown-miliseconds") - System.currentTimeMillis() + cds.get(player)) / 1000L + 1)));
            return false;
        }

        cds.put(player, System.currentTimeMillis());

        final String name = ShulkerUtils.getShulkerName(it);

        Bukkit.getScheduler().runTask(AxShulkers.getInstance(), () -> {
            final Shulkerbox shulkerbox = Shulkerboxes.getShulker(it, name);
            if (shulkerbox == null) return;

            player.openInventory(shulkerbox.getShulkerInventory());

            MessageUtils.sendMsgP(player, "open.message");

            if (!MESSAGES.getString("open.sound").isEmpty()) {
                player.playSound(player.getLocation(), Sound.valueOf(MESSAGES.getString("open.sound")), 1f, 1f);
            }
        });
        return true;
    }
}
