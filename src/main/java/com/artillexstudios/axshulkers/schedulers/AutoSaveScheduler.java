package com.artillexstudios.axshulkers.schedulers;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
            List<UUID> forRemoval = new ArrayList<>();
            for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
                AxShulkers.getDB().updateShulker(shulkerbox.getShulkerInventory().getContents(), shulkerbox.getUUID());

                boolean inactive = System.currentTimeMillis() - shulkerbox.getLastOpen() > Duration.of(30, ChronoUnit.MINUTES).toMillis();
                if (inactive && shulkerbox.getShulkerInventory().getViewers().isEmpty()) {
                    forRemoval.add(shulkerbox.getUUID());
                }
            }
            for (UUID uuid : forRemoval) {
                Shulkerboxes.getShulkerMap().remove(uuid);
            }
        }, backupMinutes, backupMinutes, TimeUnit.MINUTES);
    }

    public static void stop() {
        if (executor == null) return;
        executor.shutdown();
    }
}
