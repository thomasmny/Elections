package com.eintosti.elections.command.tabcomplete;

import com.eintosti.elections.command.Argument;
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