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
package de.eintosti.elections.command;

import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.command.subcommand.CancelSubCommand;
import de.eintosti.elections.command.subcommand.CommandSubCommand;
import de.eintosti.elections.command.subcommand.CreateSubCommand;
import de.eintosti.elections.command.subcommand.HelpSubCommand;
import de.eintosti.elections.command.subcommand.RunSubCommand;
import de.eintosti.elections.command.subcommand.SkipStageSubCommand;
import de.eintosti.elections.command.subcommand.TopFiveCommand;
import de.eintosti.elections.command.subcommand.VoteSubCommand;
import de.eintosti.elections.command.tabcomplete.ElectionsTabCompleter;
import de.eintosti.elections.util.external.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

public class ElectionsCommand implements CommandExecutor {

    private final ElectionsPlugin plugin;

    public ElectionsCommand(ElectionsPlugin plugin) {
        this.plugin = plugin;

        plugin.getCommand("elections").setExecutor(this);
        new ElectionsTabCompleter(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("You have to be a player to use this command!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            sendPluginInfo(player);
            return true;
        }

        SubCommand subCommand;
        switch (args[0].toLowerCase()) {
            case "create":
                subCommand = new CreateSubCommand(plugin);
                break;
            case "run":
                subCommand = new RunSubCommand(plugin);
                break;
            case "vote":
                subCommand = new VoteSubCommand(plugin);
                break;
            case "skipstage":
                subCommand = new SkipStageSubCommand(plugin);
                break;
            case "top5":
                subCommand = new TopFiveCommand(plugin);
                break;
            case "cancel":
                subCommand = new CancelSubCommand(plugin);
                break;
            case "command":
                subCommand = new CommandSubCommand(plugin);
                break;
            default:
                subCommand = new HelpSubCommand();
                break;
        }

        subCommand.execute(player, args);
        return true;
    }

    private void sendPluginInfo(Player player) {
        PluginDescriptionFile pdf = plugin.getDescription();
        player.sendMessage("§7§m-------------------------------------------\n" +
                "§a                           §lElections\n\n" +
                " §a● §7Author: §a" + StringUtils.join(pdf.getAuthors(), ", ") + "\n" +
                " §a● §7Version: §a" + pdf.getVersion() + "\n" +
                " §a➥ §7Help-Command §8- §a/elections help\n\n" +
                "§7§m-------------------------------------------"
        );
    }
}