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
package de.eintosti.elections.election;

import com.cryptomorin.xseries.XSound;
import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.Election;
import de.eintosti.elections.api.election.candidate.Candidate;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.election.candidate.ElectionCandidate;
import de.eintosti.elections.election.phase.AbstractPhase;
import de.eintosti.elections.election.phase.SetupPhase;
import de.eintosti.elections.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ElectionImpl implements Election {

    private final ElectionsPlugin plugin;
    private final ElectionSettings settings;

    private final Map<UUID, Candidate> nominations;
    private final Map<UUID, Candidate> playerVotes;
    private final Map<UUID, Integer> candidateVoteCount;

    private AbstractPhase phase;

    public ElectionImpl(ElectionsPlugin plugin) {
        this.plugin = plugin;
        this.settings = new ElectionSettings();

        this.nominations = new HashMap<>();
        this.playerVotes = new HashMap<>();
        this.candidateVoteCount = new HashMap<>();

        this.phase = new SetupPhase(plugin);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public ElectionSettings getSettings() {
        return settings;
    }

    @Override
    public AbstractPhase getPhase() {
        return phase;
    }

    @Override
    public boolean isActive() {
        return phase.getPhaseType() == PhaseType.NOMINATION || phase.getPhaseType() == PhaseType.VOTING;
    }

    @Override
    public void start() {
        if (phase != null && phase.getPhaseType() != PhaseType.SETUP) {
            phase.finish();
        }
        phase = new SetupPhase(plugin);
        startNextPhase();
    }

    @Override
    public void startNextPhase() {
        phase.finish();

        this.phase = phase.getNextPhase();
        if (phase != null) {
            phase.start();
        }
    }

    @Override
    public void cancelElection() {
        prematureStop();

        Bukkit.getOnlinePlayers().forEach(pl -> {
            Messages.sendMessage(pl, "election.cancel.success");
            XSound.ENTITY_ITEM_BREAK.play(pl);
        });
    }

    /**
     * Stops the Election before the {@link PhaseType#FINISHED} phase has been completed.
     */
    public void prematureStop() {
        phase.finish();
        plugin.resetElection();
    }

    @Override
    @Nullable
    public Candidate getCandidate(UUID uuid) {
        return nominations.get(uuid);
    }

    @Override
    @Nullable
    public Candidate getCandidate(String name) {
        return nominations.values().stream()
                .filter(candidate -> candidate.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Candidate getOrCreateCandidate(Player player) {
        return nominations.getOrDefault(player.getUniqueId(), new ElectionCandidate(player.getUniqueId(), player.getName()));
    }

    @Override
    public Map<UUID, Candidate> getNominations() {
        return nominations;
    }

    @Override
    public void addNomination(Candidate candidate) {
        this.nominations.put(candidate.getUniqueId(), candidate);
        this.candidateVoteCount.put(candidate.getUniqueId(), 0);
    }

    @Override
    public void removeNomination(Candidate candidate) {
        this.nominations.remove(candidate.getUniqueId());
        this.candidateVoteCount.remove(candidate.getUniqueId());
    }

    @Override
    public boolean isNominated(UUID uuid) {
        return nominations.containsKey(uuid);
    }

    @Override
    @Nullable
    public Candidate getVote(Player voter) {
        return this.playerVotes.get(voter.getUniqueId());
    }

    @Override
    public boolean hasVotedFor(Player player, @NotNull Candidate candidate) {
        Candidate vote = getVote(player);
        if (vote == null) {
            return false;
        }
        return vote.equals(candidate);
    }

    @Override
    public void voteFor(Player voter, String candidateName) {
        Candidate candidate = getCandidate(candidateName);
        if (candidate == null) {
            plugin.getLogger().severe("Invalid candidate: " + candidateName);
            return;
        }

        Candidate previousCandidate = getVote(voter);
        synchronized (candidateVoteCount) {
            if (candidate.equals(previousCandidate)) {
                int previousCandidateVoteCount = candidateVoteCount.get(previousCandidate.getUniqueId());
                candidateVoteCount.put(previousCandidate.getUniqueId(), previousCandidateVoteCount - 1);
                playerVotes.remove(voter.getUniqueId());
                return;
            }

            if (previousCandidate != null) {
                int previousCandidateVoteCount = candidateVoteCount.get(previousCandidate.getUniqueId());
                candidateVoteCount.put(previousCandidate.getUniqueId(), previousCandidateVoteCount - 1);
            }

            int currentCandidateVoteCount = candidateVoteCount.get(candidate.getUniqueId());
            int currentCandidateNewVoteCount = currentCandidateVoteCount + 1;
            candidateVoteCount.put(candidate.getUniqueId(), currentCandidateNewVoteCount);
            playerVotes.put(voter.getUniqueId(), candidate);
        }
    }

    @Override
    public Map<UUID, Integer> getCandidateVotes() {
        return candidateVoteCount;
    }

    @Override
    public List<Candidate> getTopFive() {
        return candidateVoteCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(entry -> getCandidate(entry.getKey()))
                .collect(Collectors.toList());

    }
}