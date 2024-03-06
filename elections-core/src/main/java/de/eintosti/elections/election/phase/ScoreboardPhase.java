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
package de.eintosti.elections.election.phase;

import de.eintosti.elections.api.election.phase.Phase;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.election.ElectionImpl;
import de.eintosti.elections.election.ElectionSettings;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.external.StringUtils;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A {@link Phase} which has a scoreboard showing important information.
 */
public abstract class ScoreboardPhase extends CountdownPhase implements Listener {

    private final ElectionImpl election;
    private final ElectionSettings settings;
    private final PhaseType phaseType;

    private final Map<UUID, FastBoard> scoreboards;

    private BukkitTask scoreboardTask;

    /**
     * Creates a new {@link ScoreboardPhase}.
     *
     * @param election  The election for which the scoreboard shows information about
     * @param phaseType The phase
     */
    public ScoreboardPhase(ElectionImpl election, PhaseType phaseType) {
        super(election, phaseType);

        this.election = election;
        this.settings = election.getSettings();
        this.phaseType = phaseType;

        this.scoreboards = new HashMap<>();
    }

    @Override
    public abstract AbstractPhase getNextPhase();

    @Override
    public void onStart() {
        super.onStart();
        initScoreboardTask();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        stopScoreboardTask();
    }

    /**
     * Initializes the scoreboard task.
     * Sets the scoreboard for online players and starts a task to update the scoreboards periodically.
     */
    public void initScoreboardTask() {
        if (!settings.scoreboard(phaseType).get()) {
            return;
        }

        Bukkit.getOnlinePlayers().forEach(this::setScoreboard);
        this.scoreboardTask = Bukkit.getScheduler().runTaskTimerAsynchronously(election.getPlugin(), () -> {
            scoreboards.values().forEach(this::updateBoard);
        }, 0, 20);
    }

    /**
     * Sets the scoreboard for the given player.
     *
     * @param player The player for whom the scoreboard should be set
     */
    private void setScoreboard(Player player) {
        if (!settings.scoreboard(phaseType).get()) {
            return;
        }

        FastBoard board = new FastBoard(player);
        board.updateTitle(Messages.getMessage("scoreboard.title"));
        this.scoreboards.put(player.getUniqueId(), board);
    }

    /**
     * Updates the scoreboard for the given board.
     *
     * @param board The board to update
     */
    private void updateBoard(FastBoard board) {
        board.updateLines(Messages.getMessages("scoreboard.body",
                Placeholder.component("phase", Messages.getMessage(
                        phaseType == PhaseType.NOMINATION
                                ? "scoreboard.phase.nomination"
                                : "scoreboard.phase.voting"
                )),
                Placeholder.unparsed("time", StringUtils.formatTime(getRemainingTime()))
        ));
    }

    /**
     * Stops the scoreboard task.
     */
    public void stopScoreboardTask() {
        if (!settings.scoreboard(phaseType).get()) {
            return;
        }

        if (scoreboardTask != null) {
            scoreboardTask.cancel();
        }
        Bukkit.getOnlinePlayers().forEach(this::removeScoreboard);
    }

    /**
     * Removes the scoreboard for a player.
     *
     * @param player The player whose scoreboard should be removed
     */
    private void removeScoreboard(Player player) {
        FastBoard board = this.scoreboards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        setScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeScoreboard(event.getPlayer());
    }
}