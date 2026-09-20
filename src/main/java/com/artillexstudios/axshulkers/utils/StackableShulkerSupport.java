package com.artillexstudios.axshulkers.utils;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.UUID;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

/**
 * Keeps empty shulker stacking optional without raising AxShulkers' compile-time API requirement.
 * Modern servers expose ItemMeta#setMaxStackSize; older servers simply leave this feature unavailable.
 */
public class StackableShulkerSupport {
    private static final Method SET_MAX_STACK_SIZE = findSetMaxStackSize();
    private static boolean warnedUnavailable = false;

    @Nullable
    private static Method findSetMaxStackSize() {
        try {
            return ItemMeta.class.getMethod("setMaxStackSize", Integer.class);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    public static boolean isAvailable() {
        return SET_MAX_STACK_SIZE != null;
    }

    public static boolean isEnabled() {
        return CONFIG.getBoolean("stacked-shulker-support.enabled", false);
    }

    public static boolean canApply() {
        if (!isEnabled()) return false;
        if (isAvailable()) return true;

        if (!warnedUnavailable) {
            warnedUnavailable = true;
            AxShulkers.getInstance().getLogger().warning("stacked-shulker-support is enabled, but this server does not support ItemMeta#setMaxStackSize. Empty shulkers will not be made stackable.");
        }
        return false;
    }

    public static void scanOnlinePlayers() {
        if (!canApply() || !CONFIG.getBoolean("stacked-shulker-support.scan-player-inventories", true)) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            scanPlayer(player);
        }
    }

    public static void scanPlayer(@NotNull Player player) {
        if (!canApply() || !CONFIG.getBoolean("stacked-shulker-support.scan-player-inventories", true)) return;

        scanInventory(player.getInventory());
        scanInventory(player.getEnderChest());
    }

    public static void scanInventory(@Nullable Inventory inventory) {
        if (!canApply() || inventory == null) return;

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            updateItem(inventory.getItem(slot));
        }
    }

    public static void updateItem(@Nullable ItemStack item) {
        if (!canApply() || !ShulkerUtils.isShulker(item)) return;

        UUID uuid = ShulkerUtils.getShulkerUUID(item);
        if (uuid != null) {
            handleManagedShulker(item, uuid);
            return;
        }

        if (isEmptyShulkerItem(item)) {
            setMaxStackSize(item, getEmptyStackSize());
            return;
        }

        if (item.getAmount() == 1) {
            setMaxStackSize(item, null);
        }
    }

    public static boolean migrateClosedShulker(@NotNull Shulkerbox shulkerbox) {
        if (!canApply() || !CONFIG.getBoolean("stacked-shulker-support.migrate-empty-managed-shulkers", true)) return false;
        if (!isEmpty(shulkerbox.getShulkerInventory().getContents())) {
            makeUnstackable(shulkerbox.getItem());
            return false;
        }

        migrateEmptyManagedShulker(shulkerbox.getItem(), shulkerbox.getUUID());
        return true;
    }

    public static void makeUnstackable(@Nullable ItemStack item) {
        if (!canApply() || !ShulkerUtils.isShulker(item)) return;
        if (item.getAmount() != 1) return;

        setMaxStackSize(item, null);
    }

    private static void handleManagedShulker(@NotNull ItemStack item, @NotNull UUID uuid) {
        if (!CONFIG.getBoolean("stacked-shulker-support.migrate-empty-managed-shulkers", true)) {
            makeUnstackable(item);
            return;
        }

        Shulkerbox cached = Shulkerboxes.getShulkerMap().get(uuid);
        if (cached != null) {
            // Open shulkers are still actively managed; migrate them only after the view closes.
            if (!cached.getShulkerInventory().getViewers().isEmpty()) return;

            if (isEmpty(cached.getShulkerInventory().getContents())) {
                migrateEmptyManagedShulker(item, uuid);
            } else {
                makeUnstackable(item);
            }
            return;
        }

        // Fall back to the database for managed shulkers that are not currently cached in memory.
        ItemStack[] storedItems = AxShulkers.getDB().getShulker(uuid);
        if (storedItems == null) {
            makeUnstackable(item);
            return;
        }

        if (isEmpty(storedItems)) {
            migrateEmptyManagedShulker(item, uuid);
        } else {
            makeUnstackable(item);
        }
    }

    private static void migrateEmptyManagedShulker(@NotNull ItemStack item, @NotNull UUID uuid) {
        // Empty shulkers do not need AxShulkers UUID/database tracking; removing it lets matching items stack again.
        ShulkerUtils.clearShulkerContents(item);
        ShulkerUtils.removeShulkerUUID(item);
        Shulkerboxes.removeShulkerbox(uuid);
        AxShulkers.getDatabaseQueue().submit(() -> AxShulkers.getDB().removeShulker(uuid));
        setMaxStackSize(item, getEmptyStackSize());
    }

    private static boolean isEmptyShulkerItem(@NotNull ItemStack item) {
        return isEmpty(ShulkerUtils.getShulkerItems(item));
    }

    public static boolean isEmpty(@Nullable ItemStack[] items) {
        if (items == null) return true;

        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) continue;
            return false;
        }
        return true;
    }

    private static int getEmptyStackSize() {
        int configured = CONFIG.getInt("stacked-shulker-support.empty-stack-size", 64);
        return Math.max(1, Math.min(99, configured));
    }

    private static void setMaxStackSize(@NotNull ItemStack item, @Nullable Integer maxStackSize) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || SET_MAX_STACK_SIZE == null) return;

        try {
            SET_MAX_STACK_SIZE.invoke(meta, maxStackSize);
            item.setItemMeta(meta);
        } catch (Exception ex) {
            AxShulkers.getInstance().getLogger().warning("Failed to update shulker max_stack_size: " + ex.getMessage());
        }
    }
}
