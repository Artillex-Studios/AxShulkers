package com.artillexstudios.axshulkers.cache;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.artillexstudios.axshulkers.AxShulkers.CONFIG;

public class Shulkerboxes {
    private static final ConcurrentHashMap<UUID, Shulkerbox> shulkerboxMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<UUID, Shulkerbox> getShulkerMap() {
        return shulkerboxMap;
    }
    private static final ConcurrentHashMap<UUID, UUID> playerShulkerboxMap = new ConcurrentHashMap<>();

    public static void addShulkerbox(@NotNull Shulkerbox shulkerbox) {
        shulkerboxMap.put(shulkerbox.getUUID(), shulkerbox);
    }

    public static ConcurrentHashMap<UUID, UUID> getPlayerShulkerMap() {
        return playerShulkerboxMap;
    }

    public static void removeShulkerbox(@NotNull UUID uuid) {
        shulkerboxMap.remove(uuid);
        playerShulkerboxMap.entrySet().removeIf(entry -> entry.getValue().equals(uuid));
    }

    @Nullable
    public static Shulkerbox getShulker(@NotNull ItemStack it, @NotNull String name, Player player) {
        Shulkerbox shulkerbox = getShulker(it, name);
        if (shulkerbox != null && player != null) {
            playerShulkerboxMap.put(player.getUniqueId(), shulkerbox.getUUID());
        }
        return shulkerbox;
    }

    @Nullable
    public static Shulkerbox getShulker(@NotNull ItemStack it, @NotNull String name) {
        if (!ShulkerUtils.isShulker(it)) return null;
        final UUID uuid = ShulkerUtils.getShulkerUUID(it);

        if (uuid == null) {
            // create new shulker in db and ram
            final ItemStack[] items = ShulkerUtils.getShulkerItems(it);
            final UUID newUUID = ShulkerUtils.addShulkerUUID(it);

            AxShulkers.getDatabaseQueue().submit(() -> {
                AxShulkers.getDB().saveShulker(items, newUUID);
            });

            final Inventory shulkerInv = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, name);
            shulkerInv.setContents(items);

            final Shulkerbox shulkerbox = new Shulkerbox(newUUID, shulkerInv, it, name);
            addShulkerbox(shulkerbox);

            return shulkerbox;
        }

        if (!shulkerboxMap.containsKey(uuid)) {
            // load into ram from db
            final ItemStack[] shulkerItems = AxShulkers.getDB().getShulker(uuid);
            if (shulkerItems == null) {
                if (CONFIG.getBoolean("delete-invalid-items", true))
                    ShulkerUtils.clearShulkerContents(it);
                ShulkerUtils.removeShulkerUUID(it);
                return getShulker(it, name);
            }

            final Inventory shulkerInv = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, name);
            shulkerInv.setContents(shulkerItems);

            final Shulkerbox shulkerbox = new Shulkerbox(uuid, shulkerInv, it, name);
            addShulkerbox(shulkerbox);

            return shulkerbox;
        }

        final Shulkerbox shulkerbox = shulkerboxMap.get(uuid);
        shulkerbox.setItem(it);
        return shulkerbox;
    }
}
