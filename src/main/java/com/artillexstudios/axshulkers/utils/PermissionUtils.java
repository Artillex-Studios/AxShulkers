package com.artillexstudios.axshulkers.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PermissionUtils {

    public static boolean hasPermission(@NotNull Player player, @NotNull String permission) {
        return player.hasPermission("axshulkers.admin")
                || player.hasPermission("axshulkers." + permission)
                || player.hasPermission("axshulkers.*");
    }

    public static boolean hasPermission(@NotNull CommandSender sender, @NotNull String permission) {
        return sender.hasPermission("axshulkers.admin")
                || sender.hasPermission("axshulkers." + permission)
                || sender.hasPermission("axshulkers.*");
    }
}
