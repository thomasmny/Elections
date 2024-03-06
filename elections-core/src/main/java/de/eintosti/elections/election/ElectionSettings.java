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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElectionSettings implements Settings, ConfigurationSerializable {

    private final Map<String, DataType<?>> data = new HashMap<>();

    private final DataType<String> position = register("position");

    private final DataType<Integer> nominationCountdown = register("nomination-countdown");
    private final DataType<Integer> votingCountdown = register("voting-countdown");
    private final DataType<Integer> maxStatusLength = register("max-status-length");
    private final DataType<Integer> maxCandidates = register("max-candidates");

    private final DataType<Boolean> nominationScoreboard = register("nomination-scoreboard");
    private final DataType<Boolean> nominationActionBar = register("nomination-actionbar");
    private final DataType<Boolean> nominationTitle = register("nomination-title");
    private final DataType<Boolean> nominationNotification = register("nomination-notification");

    private final DataType<Boolean> votingScoreboard = register("voting-scoreboard");
    private final DataType<Boolean> votingActionBar = register("voting-actionbar");
    private final DataType<Boolean> votingTitle = register("voting-title");
    private final DataType<Boolean> votingNotification = register("voting-notification");

    private final DataType<Boolean> candidateLimitEnabled = register("candidate-limit-enabled");

    private final DataType<List<String>> finishCommands = register("finish-commands");

    public <T> DataType<T> register(@NotNull String key) {
        return register(key, new DataType<>());
    }

    public <T> DataType<T> register(@NotNull String key, DataType<T> type) {
        this.data.put(key, type);
        return type;
    }

    public ElectionSettings() {
        this("Mayor", 1800, 1800, 24, 16,
                true, true, true, true,
                true, true, true, true,
                false, new ArrayList<>()
        );
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
    public Type<String> position() {
        return position;
    }

    @Override
    public Type<Integer> countdown(PhaseType phase) {
        return switch (phase) {
            case NOMINATION -> nominationCountdown;
            case VOTING -> votingCountdown;
            default -> throw new AssertionError("Unable to get countdown in phase " + phase);
        };
    }

    @Override
    public Type<Integer> maxStatusLength() {
        return maxStatusLength;
    }

    @Override
    public Type<Integer> maxCandidates() {
        return maxCandidates;
    }

    @Override
    public Type<Boolean> scoreboard(PhaseType phase) {
        return switch (phase) {
            case NOMINATION -> nominationScoreboard;
            case VOTING -> votingScoreboard;
            default -> throw new AssertionError("Unable to get scoreboard in phase " + phase);
        };
    }

    @Override
    public Type<Boolean> actionBar(PhaseType phase) {
        return switch (phase) {
            case NOMINATION -> nominationActionBar;
            case VOTING -> votingActionBar;
            default -> throw new AssertionError("Unable to get actionbar in phase " + phase);
        };
    }

    @Override
    public Type<Boolean> title(PhaseType phase) {
        return switch (phase) {
            case NOMINATION -> nominationTitle;
            case VOTING -> votingTitle;
            default -> throw new AssertionError("Unable to get title in phase " + phase);
        };
    }

    @Override
    public Type<Boolean> notification(PhaseType phase) {
        return switch (phase) {
            case NOMINATION -> nominationNotification;
            case VOTING -> votingNotification;
            default -> throw new AssertionError("Unable to get notification in phase " + phase);
        };
    }

    @Override
    public Type<Boolean> candidateLimitEnabled() {
        return candidateLimitEnabled;
    }

    @Override
    public Type<List<String>> finishCommands() {
        return finishCommands;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return data.entrySet().stream()
                .filter(entry -> entry.getValue().get() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getConfigFormat()));
    }

    public static class DataType<T> implements Type<T> {

        private T value;

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