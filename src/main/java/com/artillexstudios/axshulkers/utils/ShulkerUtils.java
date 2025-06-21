package com.artillexstudios.axshulkers.utils;

import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;
import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;

public class ShulkerUtils {
    private static final Set<Material> shulkers = EnumSet.of( // we don't have Tag.SHULKER_BOXES on 1.13
            Material.SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.PINK_SHULKER_BOX
    );

    public static boolean isShulker(@Nullable ItemStack it) {
        if (it == null) return false;
        return shulkers.contains(it.getType());
    }

    @NotNull
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

        final String str = NBT.get(it, nbti -> {
            return nbti.getString(CONFIG.getString("nbt-tag"));
        });

        if (str.isEmpty()) return null;

        return UUID.fromString(str);
    }

    public static void removeShulkerUUID(@NotNull ItemStack it) {
        NBT.modify(it, nbt -> {
            nbt.removeKey(CONFIG.getString("nbt-tag"));
        });
    }

    @NotNull
    public static UUID addShulkerUUID(@NotNull ItemStack it) {
        final UUID uuid = UUID.randomUUID();

        NBT.modify(it, nbt -> {
            nbt.setString(CONFIG.getString("nbt-tag"), uuid.toString());
        });
        return uuid;
    }

    public static void setShulkerContents(@NotNull ItemStack it, @NotNull Inventory inventory, boolean bypass) {
        if (!(it.getItemMeta() instanceof BlockStateMeta)) return;

        final BlockStateMeta im = (BlockStateMeta) it.getItemMeta();
        final ShulkerBox shulker = (ShulkerBox) im.getBlockState();

        if (!bypass && CONFIG.getBoolean("enable-obfuscation", false) && !CONFIG.getBoolean("auto-clear-shulkers", false)) {
            shulker.getInventory().clear();
        } else {
            shulker.getInventory().setContents(inventory.getContents());
        }

        im.setBlockState(shulker);
        it.setItemMeta(im);
    }

    public static void clearShulkerContents(@NotNull ItemStack it) {
        if (!(it.getItemMeta() instanceof BlockStateMeta)) return;

        final BlockStateMeta im = (BlockStateMeta) it.getItemMeta();
        final ShulkerBox shulker = (ShulkerBox) im.getBlockState();

        shulker.getInventory().clear();

        im.setBlockState(shulker);
        it.setItemMeta(im);
    }

    public static void setShulkerContents(@NotNull Block block, @NotNull Inventory inventory) {
        final ShulkerBox shulker = (ShulkerBox) block.getState();

        shulker.getInventory().setContents(inventory.getContents());
        block.setBlockData(shulker.getBlockData());
    }

    public static void clearShulkerContents(@NotNull Block block) {
        final ShulkerBox shulker = (ShulkerBox) block.getState();

        shulker.getInventory().clear();
        block.setBlockData(shulker.getBlockData());
    }

    public static String getShulkerName(@NotNull ItemStack it) {
        final ItemMeta meta = it.getItemMeta();

        if (meta == null || meta.getDisplayName().isEmpty())
            return ColorUtils.format(MESSAGES.getString("shulker-title"));

        return meta.getDisplayName();
    }

    @Nullable
    public static Shulkerbox hasShulkerOpen(@NotNull Player player) {
        Shulkerbox shulker = null;

        ConcurrentLinkedDeque<UUID> queue = Shulkerboxes.getPlayerShulkerMap().get(player.getUniqueId());
        if (queue == null) return null;
        for (UUID uuid : queue) {
            Shulkerbox shulkerbox = Shulkerboxes.getShulkerMap().get(uuid);
            if (shulkerbox == null || !shulkerbox.getShulkerInventory().equals(player.getOpenInventory().getTopInventory()))
                continue;
            shulker = shulkerbox;
            break;
        }
        return shulker;
    }
}
