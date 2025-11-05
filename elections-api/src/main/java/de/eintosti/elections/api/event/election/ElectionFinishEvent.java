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
import de.eintosti.elections.api.election.candidate.Candidate;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Called when an {@link Election} finishes.
 */
public class ElectionFinishEvent extends ElectionEvent {

    private final List<Candidate> winners;

    /**
     * Creates a new ElectionFinishEvent instance.
     *
     * @param election The election that has finished
     * @param winners  The list of candidates who won the election
     */
    @ApiStatus.Internal
    public ElectionFinishEvent(Election election, List<Candidate> winners) {
        super(election);

        this.winners = winners;
    }

    /**
     * Checks if there are any winners in the {@link Election}.
     *
     * @return {@code true} if there are any winners, otherwise {@code false}.
     */
    public boolean hasWinners() {
        return !winners.isEmpty();
    }

    /**
     * Gets a list of {@link Candidate}s won the {@link Election}.
     * <p>
     * If no candidates won the election, this list will be empty.
     *
     * @return A list of candidates won the election.
     */
    public List<Candidate> getWinners() {
        return winners;
    }

    /**
     * Gets the amount of votes the winning {@link Candidate} received.
     *
     * @return The amount of votes the winning candidate received, or {@code 0} if no winners.
     */
    public int getWinningVotes() {
        if (!hasWinners()) {
            return 0;
        }
        return winners.get(0).getVotes();
    }
}
