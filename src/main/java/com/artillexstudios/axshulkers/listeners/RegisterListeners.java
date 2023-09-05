package com.artillexstudios.axshulkers.listeners;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.listeners.impl.BlockPlaceListener;
import com.artillexstudios.axshulkers.listeners.impl.CommandListener;
import com.artillexstudios.axshulkers.listeners.impl.CreativeClickListener;
import com.artillexstudios.axshulkers.listeners.impl.InventoryClickListener;
import com.artillexstudios.axshulkers.listeners.impl.ShulkerOpenListener;
import org.bukkit.plugin.PluginManager;

public class RegisterListeners {
    private final AxShulkers main = AxShulkers.getInstance();
    private final PluginManager plm = main.getServer().getPluginManager();

    public void register() {
        plm.registerEvents(new ShulkerOpenListener(), main);
        plm.registerEvents(new BlockPlaceListener(), main);
        plm.registerEvents(new InventoryClickListener(), main);
        plm.registerEvents(new CreativeClickListener(), main);
        plm.registerEvents(new CommandListener(), main);
    }
}
