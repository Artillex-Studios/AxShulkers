package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.utils.MessageUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class CreativeClickListener implements Listener {

    @EventHandler
    public void onClick(@NotNull InventoryCreativeEvent event) {
        final ArrayList<UUID> uuids = new ArrayList<>();

        AxShulkers.getScheduler().runNextTick(t -> {
            for (ItemStack it : event.getWhoClicked().getInventory().getContents()) {
                if (it == null) continue;

                final UUID uuid = ShulkerUtils.getShulkerUUID(it);
                if (uuid == null) continue;

                if (uuids.contains(uuid)) {
                    ShulkerUtils.removeShulkerUUID(it);
                    MessageUtils.sendMsgP(event.getWhoClicked(), "errors.creative-copy-item");
                    continue;
                }

                uuids.add(uuid);
            }
        });

    }
}
