package com.artillexstudios.axshulkers.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        List<String> results = new ArrayList<>();

        if (args.length == 0 || (args.length == 1 && ("reload".contains(args[0]) || "clear".contains(args[0])))) {
            if ("reload".startsWith(args[0]) && !args[0].equalsIgnoreCase("reload")) {
                results.add("reload");
            }

            if ("clear".startsWith(args[0]) && !args[0].equalsIgnoreCase("clear")) {
                results.add("clear");
            }
        }

        return results;
    }
}