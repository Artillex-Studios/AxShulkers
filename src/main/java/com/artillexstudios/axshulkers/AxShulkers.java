package com.artillexstudios.axshulkers;

import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;
import com.artillexstudios.axshulkers.commands.Commands;
import com.artillexstudios.axshulkers.commands.TabComplete;
import com.artillexstudios.axshulkers.config.AbstractConfig;
import com.artillexstudios.axshulkers.config.impl.Config;
import com.artillexstudios.axshulkers.config.impl.Messages;
import com.artillexstudios.axshulkers.database.Database;
import com.artillexstudios.axshulkers.database.DatabaseQueue;
import com.artillexstudios.axshulkers.database.impl.H2;
import com.artillexstudios.axshulkers.database.impl.SQLite;
import com.artillexstudios.axshulkers.libraries.Libraries;
import com.artillexstudios.axshulkers.listeners.RegisterListeners;
import com.artillexstudios.axshulkers.schedulers.AutoSaveScheduler;
import com.artillexstudios.axshulkers.utils.ColorUtils;
import com.tcoded.folialib.FoliaLib;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.byteflux.libby.BukkitLibraryManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class AxShulkers extends JavaPlugin {
    private static AbstractConfig abstractConfig;
    private static AbstractConfig abstractMessages;
    public static YamlDocument MESSAGES;
    public static YamlDocument CONFIG;
    private static AxShulkers instance;
    private static DatabaseQueue databaseQueue;
    private static Database database;
    private static FoliaLib foliaLib;

    public static AbstractConfig getAbstractConfig() {
        return abstractConfig;
    }

    public static AbstractConfig getAbstractMessages() {
        return abstractMessages;
    }

    public static AxShulkers getInstance() {
        return instance;
    }

    public static Database getDB() {
        return database;
    }

    public static DatabaseQueue getDatabaseQueue() {
        return databaseQueue;
    }

    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }

    @Override
    public void onLoad() {
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(this, "libraries");
        libraryManager.addMavenCentral();
        libraryManager.addJitPack();
        libraryManager.addRepository("https://repo.codemc.io/repository/maven-public/");
        libraryManager.addRepository("https://repo.papermc.io/repository/maven-public/");

        for (Libraries lib : Libraries.values()) {
            libraryManager.loadLibrary(lib.getLibrary());
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        foliaLib = new FoliaLib(this);

        int pluginId = 19570;
        new Metrics(this, pluginId);

        abstractConfig = new Config();
        abstractConfig.setup();
        CONFIG = abstractConfig.getConfig();

        abstractMessages = new Messages();
        abstractMessages.setup();
        MESSAGES = abstractMessages.getConfig();

        databaseQueue = new DatabaseQueue("AxShulkers-Datastore-thread");

        switch (CONFIG.getString("database.type").toLowerCase()) {
            case "h2":
                database = new H2();
                break;
//            case "mysql":
//                database = new MySQL();
//                break;
//            case "postgresql":
//                database = new PostgreSQL();
//                break;
            default:
                database = new SQLite();
                break;
        }

        database.setup();

        new ColorUtils();
        new RegisterListeners().register();

        new AutoSaveScheduler().start();

        this.getCommand("axshulkers").setExecutor(new Commands());
        this.getCommand("axshulkers").setTabCompleter(new TabComplete());

        Bukkit.getConsoleSender().sendMessage(ColorUtils.format("&#CC00FF[AxShulkers] Loaded plugin! Using &f" + database.getType() + " &#CC00FFdatabase to store data!"));
    }

    @Override
    public void onDisable() {
        for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
            AxShulkers.getDB().updateShulker(shulkerbox.getShulkerInventory().getContents(), shulkerbox.getUUID());

            shulkerbox.close();
        }

        database.disable();
    }
}
