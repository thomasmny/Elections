package com.eintosti.elections.election;

import com.eintosti.elections.api.election.settings.Settings;
import com.eintosti.elections.api.election.settings.SettingsPhase;

import java.util.ArrayList;
import java.util.List;

public class ElectionSettings implements Settings {

    private String position = "Mayor";

    private int nominationCountdown = 1800;
    private int votingCountdown = 1800;
    private int maxStatusLength = 24;
    private int maxCandidates = 16;

    private boolean nominationScoreboard = true;
    private boolean nominationActionbar = true;
    private boolean nominationTitle = true;
    private boolean nominationNotification = true;

    private boolean votingScoreboard = true;
    private boolean votingActionbar = true;
    private boolean votingTitle = true;
    private boolean votingNotification = true;

    private boolean maxEnabled = false;
    private boolean finishCommand = true;

    private final List<String> commands = new ArrayList<>();

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getCountdown(SettingsPhase phase) {
        switch (phase) {
            case NOMINATION:
                return nominationCountdown;
            case VOTING:
                return votingCountdown;
            default:
                throw new UnsupportedOperationException("Unknown phase: " + phase);
        }
    }

    public void setCountdown(SettingsPhase phase, int countdown) {
        switch (phase) {
            case NOMINATION:
                this.nominationCountdown = countdown;
                break;
            case VOTING:
                this.votingCountdown = countdown;
                break;
            default:
                throw new UnsupportedOperationException("Unknown phase: " + phase);
        }
    }

    public int getMaxStatusLength() {
        return maxStatusLength;
    }

    public void setMaxStatusLength(int maxStatusLength) {
        this.maxStatusLength = maxStatusLength;
    }

    public int getMaxCandidates() {
        return maxCandidates;
    }

    public void setMaxCandidates(int maxCandidates) {
        this.maxCandidates = maxCandidates;
    }

    public boolean isScoreboard(SettingsPhase phase) {
        switch (phase) {
            case NOMINATION:
                return nominationScoreboard;
            case VOTING:
                return votingScoreboard;
            default:
                throw new UnsupportedOperationException("Unknown phase: " + phase);
        }
    }

    public void setScoreboard(SettingsPhase phase, boolean enabled) {
        switch (phase) {
            case NOMINATION:
                this.nominationScoreboard = enabled;
                break;
            case VOTING:
                this.votingScoreboard = enabled;
                break;
            default:
                throw new UnsupportedOperationException("Unknown phase: " + phase);
        }
    }

    public boolean isActionbar(SettingsPhase phase) {
        switch (phase) {
            case NOMINATION:
                return nominationActionbar;
            case VOTING:
                return votingActionbar;
            default:
                throw new UnsupportedOperationException("Unknown phase: " + phase);
        }
    }

    public void setActionbar(SettingsPhase phase, boolean enabled) {
        switch (phase) {
            case NOMINATION:
                this.nominationActionbar = enabled;
                break;
            case VOTING:
                this.votingActionbar = enabled;
                break;
            default:
                throw new UnsupportedOperationException("Unknown phase: " + phase);
        }
    }

    public boolean isTitle(SettingsPhase phase) {
        switch (phase) {
            case NOMINATION:
                return nominationTitle;
            case VOTING:
                return votingTitle;
            default:
                throw new UnsupportedOperationException("Unknown phase: " + phase);
        }
    }

    public void setTitle(SettingsPhase phase, boolean enabled) {
        switch (phase) {
            case NOMINATION:
                this.nominationTitle = enabled;
                break;
            case VOTING:
                this.votingTitle = enabled;
                break;
            default:
                throw new UnsupportedOperationException("Unknown phase: " + phase);
        }
    }

    public boolean isNotification(SettingsPhase phase) {
        switch (phase) {
            case NOMINATION:
                return nominationNotification;
            case VOTING:
                return votingNotification;
            default:
                throw new UnsupportedOperationException("Unknown phase: " + phase);
        }
    }

    public void setNotification(SettingsPhase phase, boolean enabled) {
        switch (phase) {
            case NOMINATION:
                this.nominationNotification = enabled;
                break;
            case VOTING:
                this.votingNotification = enabled;
                break;
            default:
                throw new UnsupportedOperationException("Unknown phase: " + phase);
        }
    }

    @Override
    public boolean isFinishCommand() {
        return finishCommand;
    }

    @Override
    public void setFinishCommand(boolean enabled) {
        this.finishCommand = enabled;
    }

    @Override
    public boolean isMaxEnabled() {
        return maxEnabled;
    }

    @Override
    public void setMaxEnabled(boolean enabled) {
        this.maxEnabled = enabled;
    }

    @Override
    public List<String> getFinishCommands() {
        return commands;
    }
}