package com.artillexstudios.axshulkers.cache;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Shulkerbox {
    private final UUID uuid;
    private final Inventory shulkerInventory;

    public Shulkerbox(UUID uuid, Inventory shulkerInventory) {

        this.uuid = uuid;
        this.shulkerInventory = shulkerInventory;
    }

    @NotNull
    public Inventory getShulkerInventory() {
        return shulkerInventory;
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }
}
