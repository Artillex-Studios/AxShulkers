package com.artillexstudios.axshulkers.config.impl;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.config.AbstractConfig;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.io.IOException;

public class Messages implements AbstractConfig {
    private YamlDocument file = null;

    @Override
    public void setup() {
        try {
            file = YamlDocument.create(new File(AxShulkers.getInstance().getDataFolder(), "messages.yml"), AxShulkers.getInstance().getResource("messages.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
            file.update();
            // fix typo
            Integer old = file.getInt("open-cooldown-miliseconds", null);
            if (old != null) {
                file.set("open-cooldown-milliseconds", old);
                file.remove("open-cooldown-miliseconds");
            }
            file.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public YamlDocument getConfig() {
        return file;
    }

    @Override
    public void reloadConfig() {
        try {
            file.reload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
