package com.artillexstudios.axshulkers.listeners.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.MessageUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;
import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;

public class PlayerInteractListener implements Listener {
    private final HashMap<Player, Long> cds = new HashMap<>();

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (!ShulkerUtils.isShulker(event.getPlayer().getInventory().getItemInMainHand())) return;
        if (CONFIG.getStringList("blacklisted-worlds").contains(event.getPlayer().getWorld().getName())) {
            MessageUtils.sendMsgP(event.getPlayer(), "errors.blacklisted-world");
            return;
        }

        if (cds.containsKey(event.getPlayer()) && System.currentTimeMillis() - cds.get(event.getPlayer()) < CONFIG.getLong("open-cooldown-miliseconds")) {
            MessageUtils.sendMsgP(event.getPlayer(), "cooldown", Map.of("%seconds%", Long.toString((CONFIG.getLong("open-cooldown-miliseconds") - System.currentTimeMillis() + cds.get(event.getPlayer())) / 1000L + 1)));
            return;
        }

        cds.put(event.getPlayer(), System.currentTimeMillis());

        event.setCancelled(true);
        MessageUtils.sendMsgP(event.getPlayer(), "open.message");

        if (!MESSAGES.getString("open.sound").isEmpty()) {
            final Sound sound = Sound.sound(Key.key(MESSAGES.getString("open.sound")), Sound.Source.MASTER, 1f, 1f);
            BukkitAudiences.create(AxShulkers.getInstance()).player(event.getPlayer()).playSound(sound);
        }

        event.getPlayer().openInventory(Shulkerboxes.getShulker(event.getPlayer().getInventory().getItemInMainHand()).getShulkerInventory());
    }
}
