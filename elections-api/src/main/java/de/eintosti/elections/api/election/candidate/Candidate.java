package com.eintosti.elections.api.election.candidate;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Candidate {

    String getName();

    UUID getUniqueId();

    @Nullable
    String getStatus();

    boolean hasStatus();

    void setStatus(String status);
}