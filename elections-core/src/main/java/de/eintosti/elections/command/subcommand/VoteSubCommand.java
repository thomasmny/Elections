package com.eintosti.elections.command.subcommand;

import com.cryptomorin.xseries.XSound;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.command.SubCommand;
import com.eintosti.elections.util.Messages;
import org.bukkit.entity.Player;

public class VoteSubCommand implements SubCommand {

    private final ElectionsPlugin plugin;

    public VoteSubCommand(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("elections.vote")) {
            Messages.sendMessage(player, "vote_noPerms");
            return;
        }

        switch (plugin.getElection().getPhase().getSettingsPhase()) {
            case VOTING:
                // Continue below
                break;
            case NOMINATION:
                Messages.sendMessage(player, "vote_notStarted");
                return;
            default:
                Messages.sendMessage(player, "vote_over");
                return;
        }

        XSound.BLOCK_CHEST_OPEN.play(player);
        player.openInventory(plugin.getVoteInventory().getInventory(player));
    }
}
