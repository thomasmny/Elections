/*
 * Copyright (c) 2018-2024, Thomas Meaney
 * Copyright (c) contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.eintosti.elections.command.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ElectionsTabCompleter extends ArgumentSorter implements TabCompleter {

    public ElectionsTabCompleter(JavaPlugin plugin) {
        plugin.getCommand("elections").setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command label, @NotNull String alias, String[] args) {
        List<String> arrayList = new ArrayList<>();

        if (args.length == 1) {
            for (Argument argument : getValidArguments(sender)) {
                addArgument(args[0], argument.getName(), arrayList);
            }
        }

        return arrayList;
    }

    private Argument[] getValidArguments(CommandSender sender) {
        return sender.hasPermission("elections.admin") ? AdminArguments.values() : PlayerArguments.values();
    }

    private enum AdminArguments implements Argument {
        CREATE("create"),
        COMMAND("command"),
        RUN("run"),
        VOTE("vote"),
        TOP_5("top5"),
        SKIP_STAGE("skipStage"),
        CANCEL("cancel");

        private final String name;

        AdminArguments(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private enum PlayerArguments implements Argument {
        HELP("help"),
        RUN("run"),
        VOTE("vote"),
        TOP_5("top5");

        private final String name;

        PlayerArguments(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}