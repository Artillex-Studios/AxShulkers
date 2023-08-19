package com.artillexstudios.axshulkers.database.impl;

import com.artillexstudios.axshulkers.database.Database;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.UUID;

public class SQLite implements Database {
    private Connection conn;

    @Override
    public String getType() {
        return "SQLite";
    }

    @Override
    public void setup() {

    }

    @Override
    public void saveShulker(@NotNull ItemStack[] items, @NotNull UUID uuid) {

    }

    @Override
    public void updateShulker(@NotNull ItemStack[] items, @NotNull UUID uuid) {

    }

    @Override
    public ItemStack[] getShulker(@NotNull UUID uuid) {
        return new ItemStack[0];
    }

    @Override
    public void removeShulker(@NotNull UUID uuid) {

    }

    @Override
    public void disable() {
        try {
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
