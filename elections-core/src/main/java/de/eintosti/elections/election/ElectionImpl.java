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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public class ElectionImpl implements Election {

    private final ElectionsPlugin plugin;

    private final Map<UUID, Candidate> nominations;
    private final Map<UUID, Candidate> votes;
    private final Map<UUID, Integer> candidateVoteCount;

    private ElectionSettings settings;
    private AbstractPhase phase;

    public ElectionImpl(ElectionsPlugin plugin) {
        this.plugin = plugin;

        this.nominations = new HashMap<>();
        this.votes = new HashMap<>();
        this.candidateVoteCount = new HashMap<>();

        this.settings = new ElectionSettings();
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
        if (phase.getPhaseType() != PhaseType.SETUP) {
            phase.finish();
        }
        phase = new SetupPhase(plugin);
        startNextPhase();
    }

    @Override
    public void startNextPhase() {
        phase.finish();

        this.phase = phase.getNextPhase();
        phase.start();
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
        return this.votes.get(voter.getUniqueId());
    }

    @Override
    public boolean hasVotedFor(Player player, Candidate candidate) {
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
                votes.remove(voter.getUniqueId());
                return;
            }

            if (previousCandidate != null) {
                int previousCandidateVoteCount = candidateVoteCount.get(previousCandidate.getUniqueId());
                candidateVoteCount.put(previousCandidate.getUniqueId(), previousCandidateVoteCount - 1);
            }

            int currentCandidateVoteCount = candidateVoteCount.get(candidate.getUniqueId());
            int currentCandidateNewVoteCount = currentCandidateVoteCount + 1;
            candidateVoteCount.put(candidate.getUniqueId(), currentCandidateNewVoteCount);
            votes.put(voter.getUniqueId(), candidate);
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
                .map(entry -> Objects.requireNonNull(getCandidate(entry.getKey())))
                .collect(Collectors.toList());

    }

    private void load() {
        this.settings = new ElectionSettings();
        this.phase = new SetupPhase(plugin);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> election = new HashMap<>();

        election.put("settings", settings.serialize());
        election.put("nominations", nominations);
        election.put("votes", votes);

        return election;
    }
}