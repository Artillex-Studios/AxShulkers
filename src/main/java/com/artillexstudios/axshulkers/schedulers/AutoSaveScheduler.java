package com.artillexstudios.axshulkers.schedulers;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoSaveScheduler {
    private static ScheduledExecutorService executor = null;

    public static void start() {
        if (executor != null) executor.shutdown();

        int backupMinutes = AxShulkers.CONFIG.getInt("auto-save-minutes", 5);

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
                AxShulkers.getDB().updateShulker(shulkerbox.getShulkerInventory().getContents(), shulkerbox.getUUID());
            }
        }, backupMinutes, backupMinutes, TimeUnit.MINUTES);
    }

    public static void stop() {
        if (executor == null) return;
        executor.shutdown();
    }
}
