package com.artillexstudios.axshulkers.database.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.database.Database;
import com.artillexstudios.axshulkers.utils.SerializationUtils;
import org.bukkit.inventory.ItemStack;
import org.h2.jdbc.JdbcConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

public class H2 implements Database {
    private JdbcConnection conn;

    @Override
    public String getType() {
        return "H2";
    }

    @Override
    public void setup() {
        try {
            conn = new JdbcConnection("jdbc:h2:./" + AxShulkers.getInstance().getDataFolder() + "/data", new Properties(), null, null, false);
            conn.setAutoCommit(true);
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
        final String sql = "INSERT INTO `axshulkers_data`(`uuid`, `inventory`) VALUES (?,?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, SerializationUtils.invToBase64(items));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateShulker(@NotNull ItemStack[] items, @NotNull UUID uuid) {
        final String sql = "UPDATE `axshulkers_data` SET `inventory`= ? WHERE `uuid` = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        final String sql = "SELECT `inventory` FROM `axshulkers_data` WHERE `uuid` = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return SerializationUtils.invFromBase64(rs.getString(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void removeShulker(@NotNull UUID uuid) {
        final String sql = "DELETE FROM `axshulkers_data` WHERE `uuid` = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
