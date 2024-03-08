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
package de.eintosti.elections.api.election.phase;

import de.eintosti.elections.api.election.Election;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Phase interface represents a phase in the {@link Election}.
 */
@NullMarked
public interface Phase extends Listener {

    /**
     * Retrieves the current phase of the election.
     *
     * @return The current phase of the election
     */
    PhaseType getPhaseType();

    /**
     * Gets the {@link Phase} which will succeed the current one.
     *
     * @return The phase that will be run after the current one
     */
    Phase getNextPhase();

    /**
     * Logic which will be run whenever the phase begins.
     */
    void onStart();

    /**
     * Logic which will be run whenever the phase end.
     */
    void onFinish();
}