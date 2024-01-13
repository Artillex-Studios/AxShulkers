package com.artillexstudios.axshulkers.schedulers;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AutoSaveScheduler {

    public void start() {
        final int backupMinutes = AxShulkers.CONFIG.getInt("auto-save-minutes", 5);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
                AxShulkers.getDB().updateShulker(shulkerbox.getShulkerInventory().getContents(), shulkerbox.getUUID());
            }
        }, backupMinutes, backupMinutes, TimeUnit.MINUTES);
    }
}
