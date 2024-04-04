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
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.api.election.settings.Settings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * The Election interface defines the properties and behaviors of an election.
 */
@NullMarked
public interface Election extends ConfigurationSerializable {

    /**
     * Gets the {@link Election} settings.
     *
     * @return The election settings
     */
    Settings getSettings();

    /**
     * Gets the currently active phase.
     *
     * @return The currently active phase
     */
    Phase getCurrentPhase();

    /**
     * Gets whether the {@link Election} is currently active.
     * <p>
     * An election is active if the current phase is {@link PhaseType#NOMINATION} or {@link PhaseType#VOTING}.
     *
     * @return {@code true} if the election is active, otherwise {@code false}
     */
    boolean isActive();

    /**
     * Starts the {@link Election}.
     *
     * @throws IllegalStateException If the election has already started
     */
    void start() throws IllegalStateException;

    /**
     * Starts the next {@link Phase} of the {@link Election}.
     * <p>
     * If the election is already {@link PhaseType#FINISHED}, it is reset.
     */
    void startNextPhase();

    /**
     * Cancels and resets the {@link Election}.
     */
    void cancelElection();

    /**
     * Gets a nominated candidate with the given uuid.
     *
     * @param uuid The uuid of the candidate
     * @return The candidate if found, otherwise {@code null}
     */
    @Nullable
    Candidate getCandidate(final UUID uuid);

    /**
     * Gets a nominated candidate with the given name.
     *
     * @param name The name of the candidate
     * @return The candidate if found, otherwise {@code null}
     */
    @Nullable
    Candidate getCandidate(final String name);

    /**
     * Gets a copy of all {@link Candidate}s which are nominated for the {@link Election}.
     * <p>
     * If you wish to nominate a candidate see {@link #nominate(Player)}.
     *
     * @return A list of all candidates which are nominated for the election.
     */
    @Unmodifiable
    List<Candidate> getCandidates();

    /**
     * Nominates the given player for the {@link Election}.
     *
     * @param player The player to nominate
     * @return The created candidate object wrapping the player
     */
    Candidate nominate(final Player player);

    /**
     * Nominates the given uuid and name pair for the {@link Election}.
     *
     * @param uuid The uuid of the player to nominate
     * @param name The name of the player to nominate
     * @return The created candidate object wrapping the player
     */
    Candidate nominate(final UUID uuid, final String name);

    /**
     * Withdraws the given candidate from the {@link Election}.
     *
     * @param candidate The candidate to withdraw
     */
    void withdraw(final Candidate candidate);

    /**
     * Withdraws the candidate with the given uuid from the {@link Election}.
     *
     * @param uuid The uuid of the candidate to withdraw
     */
    void withdraw(final UUID uuid);

    /**
     * Gets whether the given player is nominated for the {@link Election}.
     *
     * @param player The player to check
     * @return {@code true} if the player is nominated, otherwise {@code false}
     */
    boolean isNominated(final Player player);

    /**
     * Gets whether the given uuid is nominated for the {@link Election}.
     *
     * @param uuid The uuid of the player to check
     * @return {@code true} if the uuid is nominated, otherwise {@code false}
     */
    boolean isNominated(final UUID uuid);

    /**
     * Gets the candidate for whom the given player has voted for.
     *
     * @param voter The player whose vote to get
     * @return The candidate for whom the player votes for, if any, otherwise {@code null}
     */
    @Nullable
    Candidate getVote(final Player voter);

    /**
     * Gets the candidate for whom the player with given uuid has voted for.
     *
     * @param voter The uuid of the player whose vote to get
     * @return The candidate for whom the player votes for, if any, otherwise {@code null}
     */
    @Nullable
    Candidate getVote(final UUID voter);

    /**
     * Gets whether the player with the given uuid has voted for a specific candidate.
     *
     * @param uuid      The uuid of the player
     * @param candidate The candidate
     * @return {@code true} if the player has voted for the candidate, otherwise {@code false}
     */
    boolean hasVotedFor(final UUID uuid, final Candidate candidate);

    /**
     * Gets whether the given player has voted for a specific candidate.
     *
     * @param player    The player
     * @param candidate The candidate
     * @return {@code true} if the player has voted for the candidate, otherwise {@code false}
     */
    boolean hasVotedFor(final Player player, final Candidate candidate);

    /**
     * Votes a specific candidate.
     *
     * @param voter     The uuid of the voter
     * @param candidate The candidate to vote for
     */
    void voteFor(final UUID voter, final Candidate candidate);

    /**
     * Votes a specific candidate.
     *
     * @param voter     The voter
     * @param candidate The candidate to vote for
     */
    void voteFor(final Player voter, final Candidate candidate);

    /**
     * Gets a list of the top five candidates with the most votes.
     *
     * @return The top give players
     */
    @Unmodifiable
    List<Candidate> getTopFive();
}