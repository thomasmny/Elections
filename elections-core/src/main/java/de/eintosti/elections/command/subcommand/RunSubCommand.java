package com.eintosti.elections.command.subcommand;

import com.cryptomorin.xseries.XSound;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.Election;
import com.eintosti.elections.api.election.settings.Settings;
import com.eintosti.elections.command.SubCommand;
import com.eintosti.elections.util.Messages;
import org.bukkit.entity.Player;

import java.util.AbstractMap.SimpleEntry;

public class RunSubCommand implements SubCommand {

    private final ElectionsPlugin plugin;

    public RunSubCommand(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("elections.run")) {
            Messages.sendMessage(player, "run_noPerms");
            return;
        }

        final Election election = plugin.getElection();
        final Settings settings = election.getSettings();

        switch (election.getPhase().getSettingsPhase()) {
            case NOMINATION:
                // Continue below
                break;
            case VOTING:
                Messages.sendMessage(player, "run_over");
                return;
            default:
                Messages.sendMessage(player, "run_notStarted");
                return;
        }

        int maxCandidates = settings.getMaxCandidates();
        if (!settings.isMaxEnabled()
                || (maxCandidates < 1)
                || (election.getNominations().size() < maxCandidates)
                || election.isNominated(player.getUniqueId())
        ) {
            XSound.BLOCK_CHEST_OPEN.play(player);
            player.openInventory(plugin.getNominationInventory().getInventory(player));
        } else {
            Messages.sendMessage(player, "run_tooManyCandidates", new SimpleEntry<>("%maxCandidates%", maxCandidates));
        }
    }
}
