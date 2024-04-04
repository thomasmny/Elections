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
import de.eintosti.elections.api.event.election.ElectionCancelEvent;
import de.eintosti.elections.api.event.election.ElectionNextPhaseEvent;
import de.eintosti.elections.api.event.election.ElectionStartEvent;
import de.eintosti.elections.election.candidate.ElectionCandidate;
import de.eintosti.elections.election.phase.AbstractPhase;
import de.eintosti.elections.election.phase.FinishPhase;
import de.eintosti.elections.election.phase.NominationPhase;
import de.eintosti.elections.election.phase.SetupPhase;
import de.eintosti.elections.election.phase.VotingPhase;
import de.eintosti.elections.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public class ElectionImpl implements Election {

    private final ElectionsPlugin plugin;
    private final ElectionSettings settings;

    private final Map<UUID, Candidate> nominations;
    private final Map<UUID, Candidate> votes;

    private AbstractPhase currentPhase;

    public static ElectionImpl init() {
        return new ElectionImpl(new ElectionSettings(), new HashMap<>(), new HashMap<>(), PhaseType.SETUP);
    }

    public static ElectionImpl unfreeze(
            ElectionSettings settings,
            Map<UUID, Candidate> nominations,
            Map<UUID, Candidate> votes,
            PhaseType currentPhase
    ) {
        return new ElectionImpl(settings, nominations, votes, currentPhase);
    }

    private ElectionImpl(
            ElectionSettings settings,
            Map<UUID, Candidate> nominations,
            Map<UUID, Candidate> votes,
            PhaseType currentPhase
    ) {
        this.plugin = JavaPlugin.getPlugin(ElectionsPlugin.class);
        this.settings = settings;

        this.nominations = nominations;
        this.votes = votes;

        switch (currentPhase) {
            case SETUP:
                this.currentPhase = new SetupPhase(plugin, this);
                break;
            case NOMINATION:
                this.currentPhase = new NominationPhase(plugin, this);
                break;
            case VOTING:
                this.currentPhase = new VotingPhase(plugin, this);
                break;
            case FINISHED:
                this.currentPhase = new FinishPhase(plugin, this);
                break;
            default:
                throw new IllegalArgumentException("Could not find phase for '" + currentPhase + "'");
        }
        this.currentPhase.start();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public ElectionSettings getSettings() {
        return settings;
    }

    @Override
    public AbstractPhase getCurrentPhase() {
        return currentPhase;
    }

    @Override
    public boolean isActive() {
        return currentPhase.getPhaseType() == PhaseType.NOMINATION || currentPhase.getPhaseType() == PhaseType.VOTING;
    }

    @Override
    public void start() {
        if (currentPhase.getPhaseType() != PhaseType.SETUP) {
            throw new IllegalStateException("The election has already started");
        }

        this.currentPhase = new SetupPhase(plugin, this);

        ElectionStartEvent event = new ElectionStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        startNextPhase();
    }

    @Override
    public void startNextPhase() {
        AbstractPhase oldPhase = currentPhase;
        oldPhase.finish();

        currentPhase = currentPhase.getNextPhase();
        currentPhase.start();

        ElectionNextPhaseEvent event = new ElectionNextPhaseEvent(this, oldPhase.getPhaseType(), currentPhase.getPhaseType());
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void cancelElection() {
        prematureStop();

        ElectionCancelEvent event = new ElectionCancelEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        Bukkit.getOnlinePlayers().forEach(pl -> {
            Messages.sendMessage(pl, "election.cancel.success");
            XSound.ENTITY_ITEM_BREAK.play(pl);
        });
    }

    /**
     * Stops the Election before the {@link PhaseType#FINISHED} phase has been completed.
     */
    public void prematureStop() {
        currentPhase.finish();
        plugin.resetElection();
    }

    @Override
    public @Nullable Candidate getCandidate(UUID uuid) {
        return nominations.get(uuid);
    }

    @Override
    public @Nullable Candidate getCandidate(String name) {
        return nominations.values().stream()
                .filter(candidate -> candidate.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Candidate getOrCreateCandidate(Player player) {
        return nominations.getOrDefault(player.getUniqueId(), new ElectionCandidate(player.getUniqueId(), player.getName()));
    }

    @Override
    public @Unmodifiable List<Candidate> getCandidates() {
        return new ArrayList<>(nominations.values());
    }

    @Override
    public Candidate nominate(Player player) {
        return nominate(player.getUniqueId(), player.getName());
    }

    @Override
    public Candidate nominate(UUID uuid, String name) {
        Candidate candidate = new ElectionCandidate(uuid, name);
        this.nominations.put(uuid, candidate);
        return candidate;
    }

    public Candidate nominate(Candidate candidate) {
        return nominate(candidate.getUniqueId(), candidate.getName());
    }

    @Override
    public void withdraw(Candidate candidate) {
        withdraw(candidate.getUniqueId());
    }

    @Override
    public void withdraw(UUID uuid) {
        this.nominations.remove(uuid);
    }

    @Override
    public boolean isNominated(Player player) {
        return isNominated(player.getUniqueId());
    }

    @Override
    public boolean isNominated(UUID uuid) {
        return nominations.containsKey(uuid);
    }

    @Override
    public @Nullable Candidate getVote(Player voter) {
        return getVote(voter.getUniqueId());
    }

    @Override
    public @Nullable Candidate getVote(UUID voter) {
        return this.votes.get(voter);
    }

    @Override
    public boolean hasVotedFor(UUID uuid, Candidate candidate) {
        Candidate vote = getVote(uuid);
        if (vote == null) {
            return false;
        }
        return vote.equals(candidate);
    }

    @Override
    public boolean hasVotedFor(Player player, Candidate candidate) {
        return hasVotedFor(player.getUniqueId(), candidate);
    }

    @Override
    public synchronized void voteFor(UUID voter, Candidate candidate) {
        Candidate previousCandidate = getVote(voter);
        if (candidate.equals(previousCandidate)) {
            previousCandidate.removeVotes(1);
            votes.remove(voter);
            return;
        }

        if (previousCandidate != null) {
            previousCandidate.removeVotes(1);
        }

        candidate.addVotes(1);
        votes.put(voter, candidate);
    }

    @Override
    public void voteFor(Player voter, Candidate candidate) {
        voteFor(voter.getUniqueId(), candidate);
    }

    @Override
    public @Unmodifiable List<Candidate> getTopFive() {
        return getCandidates()
                .stream()
                .sorted(Comparator.comparing(Candidate::getVotes, Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> election = new HashMap<>();

        election.put("settings", settings.serialize());
        election.put("phase", currentPhase.getPhaseType().name());
        election.put("nominations", serializeNominations());
        election.put("votes", serializeVotes());

        return election;
    }

    private Object serializeNominations() {
        Map<String, Object> nominations = new HashMap<>();
        for (Candidate candidate : this.nominations.values()) {
            nominations.put(String.valueOf(candidate.getUniqueId()), candidate.serialize());
        }
        return nominations;
    }

    private Object serializeVotes() {
        Map<String, Object> votes = new HashMap<>();
        this.votes.forEach((voter, vote) -> votes.put(String.valueOf(voter), String.valueOf(vote.getUniqueId())));
        return votes;
    }
}