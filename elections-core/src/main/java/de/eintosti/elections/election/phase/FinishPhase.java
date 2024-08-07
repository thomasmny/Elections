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
package de.eintosti.elections.election.phase;

import com.cryptomorin.xseries.XSound;
import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.candidate.Candidate;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.api.event.election.ElectionFinishEvent;
import de.eintosti.elections.election.Election;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.external.StringUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public class FinishPhase extends AbstractPhase {

    private final ElectionsPlugin plugin;
    private final Election election;

    private final List<Candidate> winners;
    private final List<UUID> notified;
    private int winningCount = 0;

    public FinishPhase(ElectionsPlugin plugin, Election election) {
        this.plugin = plugin;
        this.election = election;

        this.winners = new ArrayList<>();
        this.notified = new ArrayList<>();
    }

    @Override
    public PhaseType getPhaseType() {
        return PhaseType.FINISHED;
    }

    @Override
    public AbstractPhase getNextPhase() {
        plugin.resetElection();
        return plugin.getElection().getCurrentPhase();
    }

    @Override
    public void onStart() {
        findWinners();
        preformFinishCommands();

        ElectionFinishEvent event = new ElectionFinishEvent(election, winners);
        Bukkit.getPluginManager().callEvent(event);

        notifyAllPlayers();
    }

    private void findWinners() {
        for (Candidate candidate : election.getCandidates()) {
            int numberOfVotes = candidate.getVotes();
            if (numberOfVotes < winningCount || numberOfVotes == 0) {
                continue;
            }

            if (numberOfVotes > winningCount) {
                winners.clear();
                winningCount = numberOfVotes;
            }

            winners.add(candidate);
        }
    }

    /**
     * Notifies all online players that the {@link de.eintosti.elections.api.election.Election} has finished.
     */
    private void notifyAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(pl -> notifyPlayer(pl, false));
    }

    /**
     * Notifies the given player that the {@link de.eintosti.elections.api.election.Election} has finished.
     *
     * @param player     The player to notify
     * @param wasOffline Whether the player was offline while the elections finished
     */
    private void notifyPlayer(Player player, boolean wasOffline) {
        XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.play(player);
        sendWinningMessage(player, wasOffline);
        notified.add(player.getUniqueId());
    }

    /**
     * Sends a message to the given player containing information about who won the {@link de.eintosti.elections.api.election.Election}.
     *
     * @param player     The player to send the message to
     * @param wasOffline Whether the player was offline while the elections finished
     */
    public void sendWinningMessage(Player player, boolean wasOffline) {
        if (winners.isEmpty()) {
            Messages.sendMessage(player, wasOffline
                    ? "election.finished.offline.winner.none"
                    : "election.finished.online.winner.none"
            );
            return;
        }

        if (winners.size() == 1) {
            Candidate winner = winners.get(0);
            Messages.sendMessage(player, wasOffline
                            ? "election.finished.offline.winner.single"
                            : "election.finished.online.winner.single",
                    Placeholder.unparsed("winner", winner.getName()),
                    Placeholder.unparsed("votes", String.valueOf(winner.getVotes()))
            );
            return;
        }

        String winnerNames = StringUtils.join(winners.stream().map(Candidate::getName).collect(Collectors.toList()), ", ");
        Messages.sendMessage(player, wasOffline
                        ? "election.finished.offline.winner.multiple"
                        : "election.finished.online.winner.multiple",
                Placeholder.unparsed("winners", winnerNames)
        );
    }

    private void preformFinishCommands() {
        List<String> commands = election.getSettings().finishCommands().get();
        for (Candidate winner : winners) {
            for (String command : commands) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", winner.getName()));
            }
        }
    }

    @Override
    public void onFinish() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!notified.contains(player.getUniqueId())) {
            notifyPlayer(player, true);
        }
    }
}