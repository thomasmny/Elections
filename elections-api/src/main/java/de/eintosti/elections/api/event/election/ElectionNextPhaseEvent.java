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
package de.eintosti.elections.api.event.election;

import de.eintosti.elections.api.election.Election;
import de.eintosti.elections.api.election.phase.Phase;
import de.eintosti.elections.api.election.phase.PhaseType;
import org.jetbrains.annotations.ApiStatus;

/**
 * Called when the next {@link Phase} of an {@link Election} stars.
 */
public class ElectionNextPhaseEvent extends ElectionEvent {

    private final PhaseType oldPhase;
    private final PhaseType newPhase;

    @ApiStatus.Internal
    public ElectionNextPhaseEvent(Election election, PhaseType oldPhase, PhaseType newPhase) {
        super(election);

        this.oldPhase = oldPhase;
        this.newPhase = newPhase;
    }

    public PhaseType getOldPhase() {
        return oldPhase;
    }

    public PhaseType getNewPhase() {
        return newPhase;
    }
}
