package com.eintosti.elections.election;

import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.Election;
import com.eintosti.elections.api.election.candidate.Candidate;
import com.eintosti.elections.api.election.settings.SettingsPhase;
import com.eintosti.elections.election.candidate.ElectionCandidate;
import com.eintosti.elections.election.phase.AbstractPhase;
import com.eintosti.elections.election.phase.SetupPhase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

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
        Bukkit.broadcastMessage("§a§l*** Init Election");
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
    public void start() {
        if (phase != null && phase.getSettingsPhase() != SettingsPhase.SETUP) {
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

    public void prematureStop() {
        phase.finish();
        plugin.resetElection();
    }

    @Override
    public boolean isActive() {
        return phase.getSettingsPhase() == SettingsPhase.NOMINATION || phase.getSettingsPhase() == SettingsPhase.VOTING;
    }

    public Candidate getOrCreateCandidate(Player player) {
        return nominations.getOrDefault(player.getUniqueId(), new ElectionCandidate(player.getUniqueId(), player.getName()));
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
    public boolean hasVoted(Player player, @NotNull Candidate candidate) {
        Candidate vote = getVote(player);
        if (vote == null) {
            return false;
        }
        return vote.equals(candidate);
    }

    @Override
    public void vote(Player voter, String candidateName) {
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
    public Map<UUID, Integer> getCandidateVoteCount() {
        return candidateVoteCount;
    }

    @Override
    public LinkedList<Candidate> getTop5() {
        LinkedList<Candidate> result = new LinkedList<>();

        candidateVoteCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .forEachOrdered(entry -> result.add(getCandidate(entry.getKey())));

        return result;
    }
}