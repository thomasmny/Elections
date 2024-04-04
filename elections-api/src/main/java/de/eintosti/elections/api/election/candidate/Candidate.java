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
package de.eintosti.elections.api.election.candidate;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * The Candidate interface defines the properties and behaviors of a candidate in an election.
 */
@NullMarked
public interface Candidate extends ConfigurationSerializable {

    /**
     * Retrieves the name of the candidate.
     *
     * @return The name of the candidate
     */
    String getName();

    /**
     * Retrieves the unique identifier of the candidate.
     *
     * @return The unique identifier of the candidate
     */
    UUID getUniqueId();

    /**
     * Retrieves the status of the candidate.
     *
     * @return The status of the candidate, or null if no status is set
     */
    @Nullable
    String getStatus();

    /**
     * Checks if the candidate has a status set.
     *
     * @return {@code true} if the candidate has a status set, {@code false} otherwise
     */
    boolean hasStatus();

    /**
     * Gets the amount of votes the candidate has received.
     *
     * @return The amount of votes the candidate has received
     */
    int getVotes();

    /**
     * Sets the amount of votes the candidate has received.
     *
     * @param amount The amount of votes to set to
     */
    void setVotes(int amount);

    /**
     * Adds the given amount of votes to the candidate.
     *
     * @param amount The amount of votes to add
     */
    void addVotes(int amount);

    /**
     * Removes the given amount of votes from candidate.
     *
     * @param amount The amount of votes to remove
     */
    void removeVotes(int amount);

    /**
     * Sets the status of the candidate.
     *
     * @param status The new status of the candidate
     */
    void setStatus(@Nullable String status);
}