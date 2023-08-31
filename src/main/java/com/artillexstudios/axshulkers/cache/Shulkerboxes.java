package com.artillexstudios.axshulkers.cache;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.utils.ColorUtils;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Shulkerboxes {
    private static final ConcurrentHashMap<UUID, Shulkerbox> shulkerboxMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<UUID, Shulkerbox> getShulkerMap() {
        return shulkerboxMap;
    }

    public static void addShulkerbox(@NotNull Shulkerbox shulkerbox) {
        shulkerboxMap.put(shulkerbox.getUUID(), shulkerbox);
    }

    public static void removeShulkerbox(@NotNull UUID uuid) {
        shulkerboxMap.remove(uuid);
    }

    @Nullable
    public static Shulkerbox getShulker(@NotNull ItemStack it, @NotNull String name) {
        final UUID uuid = ShulkerUtils.getShulkerUUID(it);

        if (uuid == null) {
            // create new shulker in db and ram
            final ItemStack[] items = ShulkerUtils.getShulkerItems(it);
            final UUID newUUID = ShulkerUtils.addShulkerUUID(it);

            AxShulkers.getDatabaseQueue().submit(() -> {
                AxShulkers.getDB().saveShulker(items, newUUID);
            });

            final Inventory shulkerInv = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, ColorUtils.format(name));
            shulkerInv.setContents(items);

            final Shulkerbox shulkerbox = new Shulkerbox(newUUID, shulkerInv, it);
            addShulkerbox(shulkerbox);

            return shulkerbox;
        }

        if (!shulkerboxMap.containsKey(uuid)) {
            // load into ram from db
            final ItemStack[] shulkerItems = AxShulkers.getDB().getShulker(uuid);
            if (shulkerItems == null) {
                ShulkerUtils.removeShulkerUUID(it);
                return getShulker(it, name);
            }

            final Inventory shulkerInv = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, ColorUtils.format(name));
            shulkerInv.setContents(shulkerItems);

            final Shulkerbox shulkerbox = new Shulkerbox(uuid, shulkerInv, it);
            addShulkerbox(shulkerbox);

            return shulkerbox;
        }

        final Shulkerbox shulkerbox = shulkerboxMap.get(uuid);
        shulkerbox.setItem(it);
        return shulkerbox;
    }
}
