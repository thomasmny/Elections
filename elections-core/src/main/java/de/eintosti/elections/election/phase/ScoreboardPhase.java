package com.eintosti.elections.election.phase;

import com.eintosti.elections.api.election.settings.SettingsPhase;
import com.eintosti.elections.election.ElectionImpl;
import com.eintosti.elections.election.ElectionSettings;
import com.eintosti.elections.util.Messages;
import com.eintosti.elections.util.external.StringUtils;
import com.google.common.collect.Lists;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A phase which has a scoreboard showing important information.
 *
 * @author Trichtern
 */
public abstract class ScoreboardPhase extends CountdownPhase implements Listener {

    private final ElectionImpl election;
    private final ElectionSettings settings;
    private final SettingsPhase settingPhase;

    private final Map<UUID, FastBoard> scoreboards;
    private final List<Entry<String, String>> placeholders;

    private BukkitTask scoreboardTask;

    public ScoreboardPhase(ElectionImpl election, SettingsPhase settingPhase, List<Entry<String, String>> placeholders) {
        super(election, settingPhase);

        this.election = election;
        this.settings = election.getSettings();
        this.settingPhase = settingPhase;

        this.scoreboards = new HashMap<>();
        this.placeholders = placeholders;
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

    public void initScoreboardTask() {
        if (!settings.isScoreboard(settingPhase)) {
            return;
        }

        Bukkit.getOnlinePlayers().forEach(this::setScoreboard);
        this.scoreboardTask = Bukkit.getScheduler().runTaskTimer(election.getPlugin(), () -> {
            placeholders.stream()
                    .filter(entry -> entry.getKey().equals("%time%"))
                    .forEach(entry -> entry.setValue(StringUtils.formatTime(getRemainingTime())));
            scoreboards.values().forEach(this::updateBoard);
        }, 0, 20);
    }

    private void setScoreboard(Player player) {
        if (!settings.isScoreboard(settingPhase)) {
            return;
        }

        FastBoard board = new FastBoard(player);
        board.updateTitle(Messages.getString("scoreboard_title"));
        this.scoreboards.put(player.getUniqueId(), board);
    }

    private void updateBoard(FastBoard board) {
        board.updateLines(
                Lists.newArrayList(Messages.getStringList("scoreboard_body"))
                        .stream()
                        .map(this::replacePlaceHolders)
                        .collect(Collectors.toList())
        );
    }

    private String replacePlaceHolders(String query) {
        return placeholders.stream()
                .map(entry -> (Function<String, String>) data -> data.replaceAll(entry.getKey(), entry.getValue()))
                .reduce(Function.identity(), Function::andThen)
                .apply(query);
    }

    public void stopScoreboardTask() {
        if (!settings.isScoreboard(settingPhase)) {
            return;
        }

        if (scoreboardTask != null) {
            scoreboardTask.cancel();
        }
        Bukkit.getOnlinePlayers().forEach(this::removeScoreboard);
    }

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