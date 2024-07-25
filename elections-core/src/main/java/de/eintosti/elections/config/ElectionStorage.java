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
package de.eintosti.elections.config;

import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.candidate.Candidate;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.election.Election;
import de.eintosti.elections.election.ElectionSettings;
import de.eintosti.elections.election.candidate.ElectionCandidate;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class ElectionStorage extends ConfigurationFile {

    public ElectionStorage(ElectionsPlugin plugin) {
        super(plugin, "election.yml");
    }

    public void saveElection(de.eintosti.elections.api.election.Election election) {
        getFile().set("election", election.serialize());
        saveFile();
    }

    public Election loadElection() {
        if (!this.file.exists()) {
            return Election.init();
        }

        ElectionSettings settings = parseSettings();
        PhaseType currentPhase = parseCurrentPhase();
        Map<UUID, Candidate> nominations = parseNominations();
        Map<UUID, Candidate> votes = parseVotes(nominations);

        return Election.unfreeze(settings, nominations, votes, currentPhase);
    }

    private ElectionSettings parseSettings() {
        String position = this.configuration.getString("election.settings.position", "Mayor");

        int nominationCountdown = this.configuration.getInt("election.settings.nomination-countdown", 1800);
        int votingCountdown = this.configuration.getInt("election.settings.voting-countdown", 1800);
        int maxStatusLength = this.configuration.getInt("election.settings.max-status-length", 24);
        int maxCandidates = this.configuration.getInt("election.settings.max-candidates", 16);

        boolean nominationScoreboard = this.configuration.getBoolean("election.settings.nomination-scoreboard", true);
        boolean nominationActionBar = this.configuration.getBoolean("election.settings.nomination-actionbar", true);
        boolean nominationTitle = this.configuration.getBoolean("election.settings.nomination-title", true);
        boolean nominationNotification = this.configuration.getBoolean("election.settings.nomination-notification", true);

        boolean votingScoreboard = this.configuration.getBoolean("election.settings.voting-scoreboard", true);
        boolean votingActionBar = this.configuration.getBoolean("election.settings.voting-actionbar", true);
        boolean votingTitle = this.configuration.getBoolean("election.settings.voting-title", true);
        boolean votingNotification = this.configuration.getBoolean("election.settings.voting-notification", true);
        boolean candidateLimitEnabled = this.configuration.getBoolean("election.settings.candidate-limit-enabled", false);

        List<String> finishCommands = this.configuration.getStringList("election.settings.finish-commands");

        return new ElectionSettings(position, nominationCountdown, votingCountdown, maxStatusLength, maxCandidates, nominationScoreboard, nominationActionBar, nominationTitle, nominationNotification, votingScoreboard, votingActionBar, votingTitle, votingNotification, candidateLimitEnabled, finishCommands);
    }

    private PhaseType parseCurrentPhase() {
        final PhaseType phaseType = PhaseType.fromString(this.configuration.getString("election.phase"));
        return (phaseType != null) ? phaseType : PhaseType.SETUP;
    }

    private Map<UUID, Candidate> parseNominations() {
        ConfigurationSection nominationSection = configuration.getConfigurationSection("election.nominations");
        if (nominationSection == null) {
            return new HashMap<>();
        }

        Set<String> uuidStrings = nominationSection.getKeys(false);
        if (uuidStrings.isEmpty()) {
            return new HashMap<>();
        }

        Map<UUID, Candidate> nominations = new HashMap<>();
        for (String uuidString : uuidStrings) {
            UUID uuid = UUID.fromString(uuidString);
            String name = nominationSection.getString(uuidString + ".name");
            String status = nominationSection.getString(uuidString + ".status");
            int votes = nominationSection.getInt(uuidString + ".votes");
            System.out.println("- uuid=" + uuidString + ", name=" + name + ", status=" + status + ", votes=" + votes);
            nominations.put(uuid, new ElectionCandidate(uuid, name, status, votes));
        }
        return nominations;
    }

    private Map<UUID, Candidate> parseVotes(Map<UUID, Candidate> nominations) {
        ConfigurationSection voteSection = configuration.getConfigurationSection("election.votes");
        if (voteSection == null) {
            return new HashMap<>();
        }

        Set<String> uuidStrings = voteSection.getKeys(false);
        if (uuidStrings.isEmpty()) {
            return new HashMap<>();
        }

        Map<UUID, Candidate> votes = new HashMap<>();
        for (String uuidString : uuidStrings) {
            UUID uuid = UUID.fromString(uuidString);
            UUID vote = UUID.fromString(voteSection.getString(uuidString));
            votes.put(uuid, nominations.get(vote));
        }
        return votes;
    }
}