package com.eintosti.elections.api.election;

import com.eintosti.elections.api.election.candidate.Candidate;
import com.eintosti.elections.api.election.phase.Phase;
import com.eintosti.elections.api.election.settings.Settings;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public interface Election {

    Settings getSettings();

    Phase getPhase();

    void start();

    void startNextPhase();

    boolean isActive();

    @Nullable
    Candidate getCandidate(UUID uuid);

    @Nullable
    Candidate getCandidate(String name);

    Map<UUID, Candidate> getNominations();

    void addNomination(Candidate candidate);

    void removeNomination(Candidate candidate);

    boolean isNominated(UUID uuid);

    @Nullable
    Candidate getVote(Player voter);

    boolean hasVoted(Player player, @NotNull Candidate candidate);

    void vote(Player voter, String candidateName);

    Map<UUID, Integer> getCandidateVoteCount();

    LinkedList<Candidate> getTop5();
}