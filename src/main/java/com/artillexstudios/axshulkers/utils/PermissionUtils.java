package com.artillexstudios.axshulkers.utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PermissionUtils {
    public static boolean hasPermission(@NotNull Player player, @NotNull String permission) {

        return player.hasPermission("axshulkers.admin")
                || player.hasPermission("axshulkers." + permission)
                || player.hasPermission("axshulkers.*");
    }
}
