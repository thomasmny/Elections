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
package de.eintosti.elections.election.candidate;

import de.eintosti.elections.api.election.candidate.Candidate;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NullMarked
public class ElectionCandidate implements Candidate, Serializable {

    private final UUID uuid;
    private final String name;

    @Nullable
    private String status;
    private int votes;

    public ElectionCandidate(UUID uuid, String name, @Nullable String status, int votes) {
        this.uuid = uuid;
        this.name = name;

        this.status = status;
        this.votes = 0;
    }

    public ElectionCandidate(UUID uuid, String name) {
        this(uuid, name, null, 0);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public @Nullable String getStatus() {
        return this.status;
    }

    @Override
    public boolean hasStatus() {
        return status != null;
    }

    @Override
    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    @Override
    public int getVotes() {
        return votes;
    }

    @Override
    public void setVotes(int amount) {
        this.votes = amount;
    }

    @Override
    public void addVotes(int amount) {
        this.votes += amount;
    }

    @Override
    public void removeVotes(int amount) {
        this.votes -= amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Candidate)) {
            return false;
        }

        Candidate other = (Candidate) obj;
        return this.uuid.equals(other.getUniqueId());
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> candidate = new HashMap<>();

        candidate.put("name", name);
        if (status != null) {
            candidate.put("status", status);
        }
        candidate.put("votes", votes);

        return candidate;
    }
}