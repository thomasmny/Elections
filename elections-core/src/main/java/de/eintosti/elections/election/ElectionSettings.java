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
package de.eintosti.elections.election;

import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.api.election.settings.Settings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@NullMarked
public class ElectionSettings implements Settings, ConfigurationSerializable {

    private final Map<String, ElectionSetting<?>> data = new HashMap<>();

    private final ElectionSetting<String> position = register("position", "Mayor");

    private final ElectionSetting<Integer> nominationCountdown = register("nomination-countdown", 1800);
    private final ElectionSetting<Integer> votingCountdown = register("voting-countdown", 1800);
    private final ElectionSetting<Integer> maxStatusLength = register("max-status-length", 24);
    private final ElectionSetting<Integer> maxCandidates = register("max-candidates", 16);

    private final ElectionSetting<Boolean> nominationScoreboard = register("nomination-scoreboard", true);
    private final ElectionSetting<Boolean> nominationActionBar = register("nomination-actionbar", true);
    private final ElectionSetting<Boolean> nominationTitle = register("nomination-title", true);
    private final ElectionSetting<Boolean> nominationNotification = register("nomination-notification", true);

    private final ElectionSetting<Boolean> votingScoreboard = register("voting-scoreboard", true);
    private final ElectionSetting<Boolean> votingActionBar = register("voting-actionbar", true);
    private final ElectionSetting<Boolean> votingTitle = register("voting-title", true);
    private final ElectionSetting<Boolean> votingNotification = register("voting-notification", true);

    private final ElectionSetting<Boolean> candidateLimitEnabled = register("candidate-limit-enabled", false);

    private final ElectionSetting<List<String>> finishCommands = register("finish-commands", new ArrayList<>());

    public <T> ElectionSetting<T> register(String key, T defaultValue) {
        return register(key, new ElectionSetting<>(defaultValue));
    }

    public <T> ElectionSetting<T> register(String key, ElectionSetting<T> type) {
        this.data.put(key, type);
        return type;
    }

    public ElectionSettings() {
    }

    public ElectionSettings(
            String position,
            int nominationCountdown,
            int votingCountdown,
            int maxStatusLength,
            int maxCandidates,
            boolean nominationScoreboard,
            boolean nominationActionBar,
            boolean nominationTitle,
            boolean nominationNotification,
            boolean votingScoreboard,
            boolean votingActionBar,
            boolean votingTitle,
            boolean votingNotification,
            boolean candidateLimitEnabled,
            List<String> finishCommands
    ) {
        this.position.set(position);
        this.nominationCountdown.set(nominationCountdown);
        this.votingCountdown.set(votingCountdown);
        this.maxStatusLength.set(maxStatusLength);
        this.maxCandidates.set(maxCandidates);

        this.nominationScoreboard.set(nominationScoreboard);
        this.nominationActionBar.set(nominationActionBar);
        this.nominationTitle.set(nominationTitle);
        this.nominationNotification.set(nominationNotification);

        this.votingScoreboard.set(votingScoreboard);
        this.votingActionBar.set(votingActionBar);
        this.votingTitle.set(votingTitle);
        this.votingNotification.set(votingNotification);

        this.candidateLimitEnabled.set(candidateLimitEnabled);
        this.finishCommands.set(finishCommands);
    }

    @Override
    public Setting<String> position() {
        return position;
    }

    @Override
    public Setting<Integer> countdown(PhaseType phase) {
        switch (phase) {
            case NOMINATION:
                return nominationCountdown;
            case VOTING:
                return votingCountdown;
            default:
                throw new IllegalArgumentException("Unable to get countdown for phase " + phase);
        }
    }

    @Override
    public Setting<Integer> maxStatusLength() {
        return maxStatusLength;
    }

    @Override
    public Setting<Integer> maxCandidates() {
        return maxCandidates;
    }

    @Override
    public Setting<Boolean> scoreboard(PhaseType phase) {
        switch (phase) {
            case NOMINATION:
                return nominationScoreboard;
            case VOTING:
                return votingScoreboard;
            default:
                throw new IllegalArgumentException("Unable to get scoreboard for phase " + phase);
        }
    }

    @Override
    public Setting<Boolean> actionBar(PhaseType phase) {
        switch (phase) {
            case NOMINATION:
                return nominationActionBar;
            case VOTING:
                return votingActionBar;
            default:
                throw new IllegalArgumentException("Unable to get actionbar for phase " + phase);
        }
    }

    @Override
    public Setting<Boolean> title(PhaseType phase) {
        switch (phase) {
            case NOMINATION:
                return nominationTitle;
            case VOTING:
                return votingTitle;
            default:
                throw new IllegalArgumentException("Unable to get title for phase " + phase);
        }
    }

    @Override
    public Setting<Boolean> notification(PhaseType phase) {
        switch (phase) {
            case NOMINATION:
                return nominationNotification;
            case VOTING:
                return votingNotification;
            default:
                throw new IllegalArgumentException("Unable to get notification for phase " + phase);
        }
    }

    @Override
    public Setting<Boolean> candidateLimitEnabled() {
        return candidateLimitEnabled;
    }

    @Override
    public Setting<List<String>> finishCommands() {
        return finishCommands;
    }

    @Override
    public Map<String, Object> serialize() {
        return data.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> entry.getValue().getConfigFormat())
                );
    }

    @NullMarked
    public static class ElectionSetting<T> implements Setting<T> {

        private T value;

        public ElectionSetting(T defaultValue) {
            this.value = defaultValue;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void set(T value) {
            this.value = value;
        }

        protected Object getConfigFormat() {
            return value;
        }
    }
}