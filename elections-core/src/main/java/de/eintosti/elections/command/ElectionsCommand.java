package com.eintosti.elections.command;

import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.command.subcommand.CreateSubCommand;
import com.eintosti.elections.command.subcommand.HelpSubCommand;
import com.eintosti.elections.command.subcommand.RunSubCommand;
import com.eintosti.elections.command.subcommand.SkipStageSubCommand;
import com.eintosti.elections.command.subcommand.TopFiveCommand;
import com.eintosti.elections.command.subcommand.VoteSubCommand;
import com.eintosti.elections.command.tabcomplete.ElectionsTabCompleter;
import com.eintosti.elections.util.external.StringUtils;
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
                //TODO
            case "command":
                //TODO
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