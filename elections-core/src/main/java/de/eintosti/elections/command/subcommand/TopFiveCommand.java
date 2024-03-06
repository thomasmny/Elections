package com.eintosti.elections.command.subcommand;

import com.cryptomorin.xseries.XSound;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.command.SubCommand;
import com.eintosti.elections.election.ElectionImpl;
import com.eintosti.elections.util.Messages;
import org.bukkit.entity.Player;

public class TopFiveCommand implements SubCommand {

    private final ElectionsPlugin plugin;

    public TopFiveCommand(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        final ElectionImpl election = plugin.getElection();

        switch (election.getPhase().getSettingsPhase()) {
            case VOTING:
            case FINISHED:
                // Continue below
                break;
            default:
                Messages.sendMessage(player, "vote_notStarted");
                return;
        }

        XSound.BLOCK_CHEST_OPEN.play(player);
        player.openInventory(plugin.getTopInventory().getInventory());
    }
}
