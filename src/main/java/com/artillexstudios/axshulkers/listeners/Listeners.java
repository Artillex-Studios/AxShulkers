package com.artillexstudios.axshulkers.listeners;

import com.artillexstudios.axshulkers.AxShulkers;
import com.artillexstudios.axshulkers.listeners.impl.BlockDispenseListener;
import com.artillexstudios.axshulkers.listeners.impl.BlockPlaceListener;
import com.artillexstudios.axshulkers.listeners.impl.CommandListener;
import com.artillexstudios.axshulkers.listeners.impl.CreativeClickListener;
import com.artillexstudios.axshulkers.listeners.impl.EntityDeathListener;
import com.artillexstudios.axshulkers.listeners.impl.InventoryClickListener;
import com.artillexstudios.axshulkers.listeners.impl.PlayerDropItemListener;
import com.artillexstudios.axshulkers.listeners.impl.PlayerInteractListener;
import com.artillexstudios.axshulkers.listeners.impl.PlayerMoveListener;
import com.artillexstudios.axshulkers.listeners.impl.ShulkerOpenListener;
import org.bukkit.plugin.PluginManager;

public class Listeners {

    public static void register() {
        AxShulkers main = AxShulkers.getInstance();
        PluginManager plm = main.getServer().getPluginManager();
        plm.registerEvents(new ShulkerOpenListener(), main);
        plm.registerEvents(new BlockPlaceListener(), main);
        plm.registerEvents(new InventoryClickListener(), main);
        plm.registerEvents(new CreativeClickListener(), main);
        plm.registerEvents(new CommandListener(), main);
        plm.registerEvents(new PlayerDropItemListener(), main);
        plm.registerEvents(new BlockDispenseListener(), main);
        plm.registerEvents(new EntityDeathListener(), main);
        plm.registerEvents(new PlayerMoveListener(), main);
        plm.registerEvents(new PlayerInteractListener(), main);
    }
}
