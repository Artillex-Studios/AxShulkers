package com.artillexstudios.axshulkers.utils;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

public class ShulkerUtils {

    public static boolean isShulker(@Nullable ItemStack it) {
        if (it == null) return false;
        return it.getType().toString().contains("SHULKER_BOX");
    }

    public static ItemStack[] getShulkerItems(@NotNull ItemStack it) {
        final BlockStateMeta im = (BlockStateMeta) it.getItemMeta();
        final ShulkerBox shulker = (ShulkerBox) im.getBlockState();
        final Inventory inv = shulker.getInventory();

        return inv.getContents();
    }

    @Nullable
    public static UUID getShulkerUUID(@Nullable ItemStack it) {
        if (it == null) return null;
        if (it.getType().equals(Material.AIR)) return null;

        final NBTItem nbti = new NBTItem(it);
        if (!nbti.hasTag("AxShulkers-UUID")) return null;

        return UUID.fromString(nbti.getString("AxShulkers-UUID"));
    }

    public static void removeShulkerUUID(@NotNull ItemStack it) {
        final NBTItem nbti = new NBTItem(it);
        if (!nbti.hasTag("AxShulkers-UUID")) return;

        nbti.removeKey("AxShulkers-UUID");
        it.setItemMeta(nbti.getItem().getItemMeta());
    }

    @NotNull
    public static UUID addShulkerUUID(@NotNull ItemStack it) {
        final NBTItem nbti = new NBTItem(it);
        final UUID uuid = UUID.randomUUID();

        nbti.setString("AxShulkers-UUID", uuid.toString());
        it.setItemMeta(nbti.getItem().getItemMeta());
        return uuid;
    }

    public static void setShulkerContents(@NotNull ItemStack it, @NotNull Inventory inventory, boolean bypass) {

        final BlockStateMeta im = (BlockStateMeta) it.getItemMeta();
        final ShulkerBox shulker = (ShulkerBox) im.getBlockState();

        if (!bypass && CONFIG.getBoolean("enable-obsfucation")) {
            shulker.getInventory().clear();
        } else {
            shulker.getInventory().setContents(inventory.getContents());
        }

        im.setBlockState(shulker);
        it.setItemMeta(im);
    }

    public static void setShulkerContents(@NotNull Block block, @NotNull Inventory inventory) {

        final ShulkerBox shulker = (ShulkerBox) block.getState();

        shulker.getInventory().setContents(inventory.getContents());
        block.setBlockData(shulker.getBlockData());
    }
}
