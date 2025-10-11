package com.artillexstudios.axshulkers.safety;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Notifier implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (SafetyManager.isSafe()) return;
        if (!event.getPlayer().hasPermission("*") && !event.getPlayer().isOp()) return;
        event.getPlayer().sendMessage(ColorUtils.format("\n&#FF6600⚠ &#FF0000[%s] We have disabled some (or all) features for safety reasons!\n&#DDDDDDThis is done to protect your server, please update the plugin to resolve this issue.\n".formatted(AxShulkers.getInstance().getName())));
    }

    public void sendAlert() {
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format("\n&#FF6600⚠ &#FF0000[%s] We have disabled some (or all) features for safety reasons!\n&#DDDDDDThis is done to protect your server, please update the plugin to resolve this issue.\n".formatted(AxShulkers.getInstance().getName())));
    }
}
