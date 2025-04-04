package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

public class EntityDeathListener implements Listener {

    @EventHandler (ignoreCancelled = true)
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Item)) return;

        ItemStack it = ((Item) event.getEntity()).getItemStack();
        if (!ShulkerUtils.isShulker(it)) return;

        if (CONFIG.getBoolean("undestroyable-shulkers")) {
            event.setCancelled(true);
            return;
        }

        String name = ShulkerUtils.getShulkerName(it);
        Shulkerbox shulkerbox = Shulkerboxes.getShulker(it, name);
        if (shulkerbox == null) return;

        if (it.getAmount() != 1) {
            it.setAmount(1);
            event.setCancelled(true);
            return;
        }

        shulkerbox.close();
        ShulkerUtils.setShulkerContents(it, shulkerbox.getShulkerInventory(), true);
    }

}
