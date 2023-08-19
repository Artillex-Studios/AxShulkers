package com.artillexstudios.axshulkers.schedulers;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.cache.Shulkerbox;
import com.artillexstudios.axshulkers.cache.Shulkerboxes;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AutoSaveScheduler {

    public void start() {

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            AxShulkers.getDatabaseQueue().submit(() -> {
                for (Shulkerbox shulkerbox : Shulkerboxes.getShulkerMap().values()) {
                    AxShulkers.getDB().updateShulker(shulkerbox.getShulkerInventory().getContents(), shulkerbox.getUUID());
                }
            });
        }, AxShulkers.CONFIG.getLong("auto-save-minutes"), AxShulkers.CONFIG.getLong("auto-save-minutes"), TimeUnit.MINUTES);
    }
}
