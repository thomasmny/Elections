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
import org.jspecify.annotations.NullMarked;

/**
 * The different {@link Phase}s of an {@link Election}.
 *
 * <p>
 * This enumeration defines the different phases of an election, such as the
 * <ul>
 *   <li>Setup phase,</li>
 *   <li>Nomination phase,</li>
 *   <li>Voting phase,</li>
 *   <li>Finish phase</li>
 * </ul>
 * Each phase has a specific purpose and behavior within the election process.
 */
@NullMarked
public enum PhaseType {

    /**
     * Represents the setup {@link Phase} of an {@link Election}.
     *
     * <p>
     * The {@code SETUP} phase is the initial phase of an election. During this phase,
     * the necessary preparations are made before the election can proceed to the
     * next phase.
     * </p>
     */
    SETUP,

    /**
     * Represents the nomination {@link Phase} of an {@link Election}.
     *
     * <p>
     * During the {@code NOMINATION} phase, eligible candidates are nominated for the election.
     * Nominations can be made by the candidates themselves.
     * </p>
     */
    NOMINATION,

    /**
     * Represents the voting {@link Phase} of an {@link Election}.
     *
     * <p>
     * During the {@code VOTING} phase, eligible voters can cast their votes for the
     * candidates in the election. The votes are recorded and stored for later counting.
     * </p>
     */
    VOTING,

    /**
     * Represents the finished {@link Phase} of an {@link Election}.
     *
     * <p>
     * The {@code FINISHED} phase is the final phase of an election. During this phase,
     * all the votes are counted and the final results are generated. No further actions
     * can be taken in this phase.
     * </p>
     */
    FINISHED
}