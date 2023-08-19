package com.artillexstudios.axshulkers.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {
    final List<String> results = new ArrayList<>();

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        results.clear();

        if (args.length == 0 || (args.length == 1 && ("reload".contains(args[0]) || "clear".contains(args[0])))) {
            if ("reload".contains(args[0]) && !args[0].equalsIgnoreCase("reload")) {
                results.add("reload");
            }

            if ("clear".contains(args[0]) && !args[0].equalsIgnoreCase("clear")) {
                results.add("clear");
            }
        }

        return results;
    }
}