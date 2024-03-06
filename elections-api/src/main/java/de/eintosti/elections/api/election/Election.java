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
package de.eintosti.elections.api.election;

import de.eintosti.elections.api.election.candidate.Candidate;
import de.eintosti.elections.api.election.phase.Phase;
import de.eintosti.elections.api.election.settings.Settings;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Election {

    Settings getSettings();

    Phase getPhase();

    boolean isActive();

    void start();

    void startNextPhase();

    void cancelElection();

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

    boolean hasVotedFor(Player player, @NotNull Candidate candidate);

    void voteFor(Player voter, String candidateName);

    Map<UUID, Integer> getCandidateVotes();

    List<Candidate> getTopFive();
}