package com.artillexstudios.axshulkers.schedulers;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutoSaveScheduler {
    private static ScheduledFuture<?> future = null;

    public static void start() {
        if (future != null) future.cancel(true);

        final int backupMinutes = AxShulkers.CONFIG.getInt("auto-save-minutes", 5);

        future = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
                AxShulkers.getDB().updateShulker(shulkerbox.getShulkerInventory().getContents(), shulkerbox.getUUID());
            }
        }, backupMinutes, backupMinutes, TimeUnit.MINUTES);
    }

    public static void stop() {
        future.cancel(true);
    }
}
