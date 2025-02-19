package com.artillexstudios.axshulkers.database.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.database.Database;
import com.artillexstudios.axshulkers.utils.SerializationUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLite implements Database {
    private Connection conn;

    @Override
    public String getType() {
        return "SQLite";
    }

    @Override
    public void setup() {

        try {
            Class.forName("org.sqlite.JDBC");
            this.conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s/data.db", AxShulkers.getInstance().getDataFolder()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `axshulkers_data` ( `uuid` VARCHAR(36) NOT NULL, `inventory` VARCHAR NOT NULL, PRIMARY KEY (`uuid`) );";
        try (PreparedStatement stmt = conn.prepareStatement(CREATE_TABLE)) {
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void saveShulker(@NotNull ItemStack[] items, @NotNull UUID uuid) {
        final String txt = "INSERT INTO `axshulkers_data`(`uuid`, `inventory`) VALUES (?,?);";
        try (PreparedStatement stmt = conn.prepareStatement(txt)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, SerializationUtils.invToBase64(items));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateShulker(@NotNull ItemStack[] items, @NotNull UUID uuid) {
        final String txt = "UPDATE `axshulkers_data` SET `inventory`= ? WHERE `uuid` = ?";
        try (PreparedStatement stmt = conn.prepareStatement(txt)) {
            stmt.setString(2, uuid.toString());
            stmt.setString(1, SerializationUtils.invToBase64(items));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    @Nullable
    public ItemStack[] getShulker(@NotNull UUID uuid) {
        final String txt = "SELECT `inventory` FROM `axshulkers_data` WHERE `uuid` = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(txt)) {
            stmt.setString(1, uuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return SerializationUtils.invFromBase64(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void removeShulker(@NotNull UUID uuid) {
        final String txt = "DELETE FROM `axshulkers_data` WHERE `uuid` = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(txt)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
