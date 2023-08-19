package com.artillexstudios.axshulkers.listeners;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.listeners.impl.BlockPlaceListener;
import com.artillexstudios.axshulkers.listeners.impl.CreativeClickListener;
import com.artillexstudios.axshulkers.listeners.impl.InventoryClickListener;
import com.artillexstudios.axshulkers.listeners.impl.PlayerInteractListener;
import org.bukkit.plugin.PluginManager;

public class RegisterListeners {
    private final AxShulkers main = AxShulkers.getInstance();
    private final PluginManager plm = main.getServer().getPluginManager();

    public void register() {
        plm.registerEvents(new PlayerInteractListener(), main);
        plm.registerEvents(new BlockPlaceListener(), main);
        plm.registerEvents(new InventoryClickListener(), main);
        plm.registerEvents(new CreativeClickListener(), main);
    }
}
