package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

public class PlayerMoveListener implements Listener {

    @EventHandler (ignoreCancelled = true)
    public void onMove(@NotNull PlayerMoveEvent event) {
        if (!CONFIG.getBoolean("disable-moving-while-open", false)) return;
        if (!event.getPlayer().getOpenInventory().getTopInventory().getType().equals(InventoryType.SHULKER_BOX)) return;
        if (event.getTo() == null) return;
        if (event.getFrom().distanceSquared(event.getTo()) == 0) return;

        MessageUtils.sendMsgP(event.getPlayer(), "errors.moving-disabled");
        event.getPlayer().closeInventory();
    }

}
