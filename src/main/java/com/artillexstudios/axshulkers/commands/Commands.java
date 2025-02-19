package com.artillexstudios.axshulkers.commands;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.utils.ColorUtils;
import com.artillexstudios.axshulkers.utils.MessageUtils;
import com.artillexstudios.axshulkers.utils.PermissionUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                if (!PermissionUtils.hasPermission(sender, "reload")) {
                    MessageUtils.sendMsgP(sender, "errors.no-permission");
                    return true;
                }

                AxShulkers.getAbstractConfig().reloadConfig();
                AxShulkers.getAbstractMessages().reloadConfig();

                for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
                    AxShulkers.getDB().updateShulker(shulkerbox.getShulkerInventory().getContents(), shulkerbox.getUUID());
                    shulkerbox.updateGuiTitle();
                }

                MessageUtils.sendMsgP(sender, "reloaded");
                return true;
            }

            if (args[0].equals("clear")) {
                if (!PermissionUtils.hasPermission(sender, "clear")) {
                    MessageUtils.sendMsgP(sender, "errors.no-permission");
                    return true;
                }

                final ItemStack it = ((Player) sender).getInventory().getItemInMainHand();
                final UUID uuid = ShulkerUtils.getShulkerUUID(it);
                if (uuid == null) {
                    MessageUtils.sendMsgP(sender, "errors.no-shulker");
                    return true;
                }

                final ItemMeta meta = it.getItemMeta();
                final String name = meta == null ? ColorUtils.format(MESSAGES.getString("shulker-title")) : meta.getDisplayName();

                ShulkerUtils.setShulkerContents(it, Shulkerboxes.getShulker(it, name).getShulkerInventory(), true);

                ShulkerUtils.removeShulkerUUID(it);
                Shulkerboxes.removeShulkerbox(uuid);
                AxShulkers.getDB().removeShulker(uuid);

                MessageUtils.sendMsgP(sender, "cleared");
                return true;
            }
        }

        MessageUtils.sendListMsg(sender, "help");
        return true;
    }
}
