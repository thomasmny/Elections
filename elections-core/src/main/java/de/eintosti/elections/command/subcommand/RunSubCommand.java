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
package de.eintosti.elections.command.subcommand;

import com.cryptomorin.xseries.XSound;
import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.Election;
import de.eintosti.elections.api.election.settings.Settings;
import de.eintosti.elections.command.SubCommand;
import de.eintosti.elections.messages.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RunSubCommand implements SubCommand {

    private final ElectionsPlugin plugin;

    public RunSubCommand(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("elections.run")) {
            Messages.sendMessage(player, "election.run.no_permission");
            return;
        }

        final Election election = plugin.getElection();
        switch (election.getCurrentPhase().getPhaseType()) {
            case NOMINATION:
                // Continue below
                break;
            case VOTING:
                Messages.sendMessage(player, "election.run.over");
                return;
            default:
                Messages.sendMessage(player, "election.run.not_started");
                return;
        }

        NominationResult nominationResult = canNominate(election, player);
        switch (nominationResult) {
            case NO_PERMISSION:
                Messages.sendMessage(player, "election.run.no_permission");
                break;
            case TOO_MANY_CANDIDATES:
                Messages.sendMessage(player, "election.run.too_many_players",
                        Placeholder.unparsed("amount", String.valueOf(election.getSettings().maxCandidates().get()))
                );
                break;
            case ALREADY_NOMINATED:
            case ALLOWED:
                XSound.BLOCK_CHEST_OPEN.play(player);
                player.openInventory(plugin.getRunInventory().getInventory(player));
                break;
        }
    }

    /**
     * Gets whether the given player is allowed to nominate themselves.
     * <p>
     * A player is allowed to nominate themselves if
     * <ul>
     *   <li>They have the permission {@code elections.run}</li>
     *   <li>They have not yet been nominated</li>
     *   <li>The maximum amount of nominations has not yet been reached (only if enabled)</li>
     * </ul>
     *
     * @param election The election in which the player wants to nominate themselves
     * @param player   The player who want to nominate themselves
     * @return The result of the nomination attempt
     */
    private NominationResult canNominate(Election election, Player player) {
        if (!player.hasPermission("elections.run")) {
            return NominationResult.NO_PERMISSION;
        }

        if (election.isNominated(player.getUniqueId())) {
            return NominationResult.ALREADY_NOMINATED;
        }

        Settings settings = election.getSettings();
        if (!settings.candidateLimitEnabled().get()) {
            return NominationResult.ALLOWED;
        }

        int maxCandidates = settings.maxCandidates().get();
        if (maxCandidates > 0 && election.getCandidates().size() < maxCandidates) {
            return NominationResult.ALLOWED;
        }

        return NominationResult.TOO_MANY_CANDIDATES;
    }

    private enum NominationResult {

        /**
         * The player does not have the necessary permission ({@code elections.run}).
         */
        NO_PERMISSION,

        /**
         * The player has already been nominated.
         */
        ALREADY_NOMINATED,

        /**
         * There are already too many nominated players.
         *
         * @see Settings#candidateLimitEnabled()
         * @see Settings#maxCandidates()
         */
        TOO_MANY_CANDIDATES,

        /**
         * The player is allowed to nominate themselves.
         */
        ALLOWED
    }
}
