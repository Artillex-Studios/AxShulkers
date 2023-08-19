package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.MessageUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

public class PlayerInteractListener implements Listener {
    private final HashMap<Player, Long> cds = new HashMap<>();

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (!ShulkerUtils.isShulker(event.getPlayer().getInventory().getItemInMainHand())) return;

        if (cds.containsKey(event.getPlayer()) && System.currentTimeMillis() - cds.get(event.getPlayer()) < CONFIG.getLong("open-cooldown-miliseconds")) {
            MessageUtils.sendMsgP(event.getPlayer(), "cooldown", Map.of("%seconds%", Long.toString((CONFIG.getLong("open-cooldown-miliseconds") - System.currentTimeMillis() + cds.get(event.getPlayer())) / 1000L + 1)));
            return;
        }

        cds.put(event.getPlayer(), System.currentTimeMillis());

        event.setCancelled(true);
        event.getPlayer().openInventory(Shulkerboxes.getShulker(event.getPlayer().getInventory().getItemInMainHand()).getShulkerInventory());
    }
}
