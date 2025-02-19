package com.artillexstudios.axshulkers.config;

import dev.dejvokep.boostedyaml.YamlDocument;

public interface AbstractConfig {

    void setup();

    YamlDocument getConfig();

    void reloadConfig();
}
