package com.eintosti.elections.command.subcommand;

import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.Election;
import com.eintosti.elections.command.SubCommand;
import com.eintosti.elections.util.Messages;
import org.bukkit.entity.Player;

public class SkipStageSubCommand implements SubCommand {

    private final Election election;

    public SkipStageSubCommand(ElectionsPlugin plugin) {
        this.election = plugin.getElection();
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("elections.skipstage")) {
            Messages.sendMessage(player, "skipStage_noPerms");
            return;
        }

        if (!election.isActive()) {
            Messages.sendMessage(player, "skipStage_notStarted");
            return;
        }

        election.startNextPhase();
    }
}
