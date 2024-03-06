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
package de.eintosti.elections.api.election.settings;

import de.eintosti.elections.api.election.Election;
import de.eintosti.elections.api.election.candidate.Candidate;
import de.eintosti.elections.api.election.phase.PhaseType;

import java.util.List;

public interface Settings {

    /**
     * Gets the position for which {@link Candidate}s will be running.
     *
     * @return The position for which candidates will be running
     */
    Type<String> position();

    /**
     * Get the amount of seconds left in the countdown for a given phase.
     *
     * @param phase The phase
     * @return The seconds left in the countdown
     */
    Type<Integer> countdown(PhaseType phase);

    /**
     * Get the maximum amount of characters a {@link Candidate}s status can be long.
     *
     * @return The maximum length of a candidate's status
     */
    Type<Integer> maxStatusLength();

    /**
     * Get the maximum amount of {@link Candidate}s that can participate in an {@link Election}.
     *
     * @return The maximum amount of candidates that can participate in an Election
     */
    Type<Integer> maxCandidates();

    /**
     * Get whether the scoreboard is enabled for the given {@link Election} phase.
     *
     * @param phase The phase
     * @return A boolean type whether the scoreboard is enabled
     */
    Type<Boolean> scoreboard(PhaseType phase);

    /**
     * Get whether the action-bar is used for displaying the remaining time in the given {@link Election} phase.
     *
     * @param phase The phase
     * @return A boolean type whether the action-bar is enabled
     */
    Type<Boolean> actionBar(PhaseType phase);

    /**
     * Get whether titles are used for notification for the given {@link Election} phase.
     *
     * @param phase The phase
     * @return A boolean type whether title notification is enabled
     */
    Type<Boolean> title(PhaseType phase);

    /**
     * Get whether players receive a chat notification about the given {@link Election} phase.
     *
     * @param phase The phase
     * @return A boolean type whether players receive a chat notification
     */
    Type<Boolean> notification(PhaseType phase);

    /**
     * Gets whether the {@link Election} has a maximum candidate limit enabled.
     *
     * @return A boolean type whether the limit is enabled
     */
    Type<Boolean> candidateLimitEnabled();

    Type<List<String>> finishCommands();

    interface Type<T> {

        /**
         * Gets the current value.
         *
         * @return The current value
         */
        T get();

        /**
         * Sets the current value.
         *
         * @param value The value to set to
         */
        void set(T value);
    }
}
