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
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Settings interface provides a way to access and modify the settings of an {@link Election}.
 */
@NullMarked
public interface Settings {

    /**
     * Gets the position for which {@link Candidate}s will be running.
     *
     * @return The position for which candidates will be running
     */
    Setting<String> position();

    /**
     * Gets the amount of seconds left in the countdown for a given phase.
     *
     * @param phase The phase
     * @return The seconds left in the countdown
     */
    Setting<Integer> countdown(final PhaseType phase);

    /**
     * Gets the maximum amount of characters a {@link Candidate}s status can be long.
     *
     * @return The maximum length of a candidate's status
     */
    Setting<Integer> maxStatusLength();

    /**
     * Gets the maximum amount of {@link Candidate}s that can participate in an {@link Election}.
     *
     * @return The maximum amount of candidates that can participate in an Election
     */
    Setting<Integer> maxCandidates();

    /**
     * Gets whether the scoreboard is enabled for the given {@link Election} phase.
     *
     * @param phase The phase
     * @return A boolean type whether the scoreboard is enabled
     */
    Setting<Boolean> scoreboard(final PhaseType phase);

    /**
     * Gets whether the action-bar is used for displaying the remaining time in the given {@link Election} phase.
     *
     * @param phase The phase
     * @return A boolean type whether the action-bar is enabled
     */
    Setting<Boolean> actionBar(final PhaseType phase);

    /**
     * Gets whether titles are used for notification for the given {@link Election} phase.
     *
     * @param phase The phase
     * @return A boolean type whether title notification is enabled
     */
    Setting<Boolean> title(final PhaseType phase);

    /**
     * Gets whether players receive a chat notification about the given {@link Election} phase.
     *
     * @param phase The phase
     * @return A boolean type whether players receive a chat notification
     */
    Setting<Boolean> notification(final PhaseType phase);

    /**
     * Gets whether the {@link Election} has a maximum candidate limit enabled.
     *
     * @return A boolean type whether the limit is enabled
     */
    Setting<Boolean> candidateLimitEnabled();

    /**
     * Gets the list of commands which will be run when the {@link Election} finishes.
     * <p>
     * Commands should <b>not</b> start with a {@code /}.
     *
     * @return The list of commands which will bre run when the election finishes
     */
    Setting<List<String>> finishCommands();

    /**
     * A container for storing a setting.
     *
     * @param <T> The type of the setting
     */
    @NullMarked
    interface Setting<T> {

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
