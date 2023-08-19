package com.artillexstudios.axshulkers.commands;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.MessageUtils;
import com.artillexstudios.axshulkers.utils.PermissionUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) sender;

        if (args.length == 1 && args[0].equals("reload")) {
            if (!PermissionUtils.hasPermission(player, "reload")) {
                MessageUtils.sendMsgP(sender, "errors.no-permission");
                return true;
            }

            AxShulkers.getAbstractConfig().reloadConfig();
            AxShulkers.getAbstractMessages().reloadConfig();

            MessageUtils.sendMsgP(sender, "reloaded");
            return true;
        }

        if (args.length == 1 && args[0].equals("clear")) {
            if (!PermissionUtils.hasPermission(player, "clear")) {
                MessageUtils.sendMsgP(sender, "errors.no-permission");
                return true;
            }

            final ItemStack it = ((Player) sender).getInventory().getItemInMainHand();
            final UUID uuid = ShulkerUtils.getShulkerUUID(it);
            if (uuid == null) {
                MessageUtils.sendMsgP(sender, "errors.no-shulker");
                return true;
            }

            ShulkerUtils.setShulkerContents(it, Shulkerboxes.getShulker(it).getShulkerInventory(), true);

            ShulkerUtils.removeShulkerUUID(it);
            Shulkerboxes.removeShulkerbox(uuid);
            AxShulkers.getDB().removeShulker(uuid);

            MessageUtils.sendMsgP(sender, "cleared");
            return true;
        }

        MessageUtils.sendListMsg(sender, "help");
        return true;
    }
}
