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

import java.util.HashMap;
import java.util.UUID;

import static com.artillexstudios.axshulkers.AxShulkers.MESSAGES;

public class Shulkerboxes {
    private static final HashMap<UUID, Shulkerbox> shulkerboxMap = new HashMap<>();

    public static HashMap<UUID, Shulkerbox> getShulkerMap() {
        return shulkerboxMap;
    }

    public static void addShulkerbox(@NotNull Shulkerbox shulkerbox) {
        shulkerboxMap.put(shulkerbox.getUUID(), shulkerbox);
    }

    public static void removeShulkerbox(@NotNull UUID uuid) {
        shulkerboxMap.remove(uuid);
    }

    @Nullable
    public static Shulkerbox getShulker(@NotNull ItemStack it) {
        final UUID uuid = ShulkerUtils.getShulkerUUID(it);

        if (uuid == null) {
            // create new shulker in db and ram
            final ItemStack[] items = ShulkerUtils.getShulkerItems(it);
            final UUID newUUID = ShulkerUtils.addShulkerUUID(it);

            AxShulkers.getDatabaseQueue().submit(() -> {
                AxShulkers.getDB().saveShulker(items, newUUID);
            });

            final Inventory shulkerInv = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, ColorUtils.format(MESSAGES.getString("shulker-title")));
            shulkerInv.setContents(items);

            final Shulkerbox shulkerbox = new Shulkerbox(newUUID, shulkerInv);
            addShulkerbox(shulkerbox);

            return shulkerbox;
        }

        if (!shulkerboxMap.containsKey(uuid)) {
            // load into ram from db
            final ItemStack[] shulkerItems = AxShulkers.getDB().getShulker(uuid);
            if (shulkerItems == null) {
                ShulkerUtils.removeShulkerUUID(it);
                return getShulker(it);
            }

            final Inventory shulkerInv = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, ColorUtils.format(MESSAGES.getString("shulker-title")));
            shulkerInv.setContents(shulkerItems);

            final Shulkerbox shulkerbox = new Shulkerbox(uuid, shulkerInv);
            addShulkerbox(shulkerbox);

            return shulkerbox;
        }

        return shulkerboxMap.get(uuid);
    }
}
