package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class CommandListener implements Listener {

    @EventHandler (ignoreCancelled = true)
    public void onCommand(@NotNull PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().getOpenInventory().getTopInventory().getType().equals(InventoryType.SHULKER_BOX)) return;

        event.setCancelled(true);
        MessageUtils.sendMsgP(event.getPlayer(), "errors.cant-run-commands");
    }

}
