package com.artillexstudios.axshulkers.utils;

import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;
import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;

public class MessageUtils {

    public static void sendMsgP(CommandSender player, String path) {
        if (MESSAGES.getString(path).isEmpty()) return;
        player.sendMessage(ColorUtils.format(CONFIG.getString("prefix") + MESSAGES.getString(path)));
    }

    public static void sendMsgP(CommandSender player, String path, Map<String, String> replacements) {
        if (MESSAGES.getString(path).isEmpty()) return;
        AtomicReference<String> message = new AtomicReference<>(MESSAGES.getString(path));
        replacements.forEach((key, value) -> message.set(message.get().replace(key, value)));
        player.sendMessage(ColorUtils.format(CONFIG.getString("prefix") + message));
    }

    public static void sendMsg(CommandSender player, String path) {
        if (MESSAGES.getString(path).isEmpty()) return;
        player.sendMessage(ColorUtils.format(MESSAGES.getString(path)));
    }

    public static void sendListMsg(CommandSender player, String path) {
        for (String m : MESSAGES.getStringList(path)) {
            player.sendMessage(ColorUtils.format(m));
        }
    }
}