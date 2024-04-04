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
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an {@link Election} related event.
 */
@NullMarked
public abstract class ElectionEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Election election;

    @ApiStatus.Internal
    public ElectionEvent(Election election) {
        this.election = election;
    }

    /**
     * Gets the {@link Election} involved in this event
     *
     * @return The election involved in this event
     */
    public Election getElection() {
        return election;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}