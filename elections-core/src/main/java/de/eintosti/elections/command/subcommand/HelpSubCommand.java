package com.eintosti.elections.command.subcommand;

import com.eintosti.elections.command.SubCommand;
import com.eintosti.elections.util.Messages;
import org.bukkit.entity.Player;

public class HelpSubCommand implements SubCommand {

    @Override
    public void execute(Player player, String[] args) {
        if (player.hasPermission("elections.admin")) {
            Messages.sendMessage(player, "help_admin");
        } else {
            Messages.sendMessage(player, "help_player");
        }
    }
}
