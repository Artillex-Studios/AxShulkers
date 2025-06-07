package com.artillexstudios.axshulkers.database;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Database {

    String getType();

    void setup();

    void saveShulker(@NotNull ItemStack[] items, @NotNull UUID uuid);

    void updateShulker(@NotNull ItemStack[] items, @NotNull UUID uuid);

    @Nullable ItemStack[] getShulker(@NotNull UUID uuid);

    void removeShulker(@NotNull UUID uuid);

    void disable();
}
