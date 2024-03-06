package com.eintosti.elections.election.candidate;

import com.eintosti.elections.api.election.candidate.Candidate;

import java.io.Serializable;
import java.util.UUID;

public class ElectionCandidate implements Candidate, Serializable {

    private final UUID uuid;
    private final String name;
    private String status;

    public ElectionCandidate(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
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
    public String getStatus() {
        return this.status;
    }

    @Override
    public boolean hasStatus() {
        return status != null;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Candidate)) {
            return false;
        }

        Candidate other = (Candidate) obj;
        return this.uuid.equals(other.getUniqueId());
    }
}