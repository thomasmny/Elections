package com.eintosti.elections.election.phase;

import com.cryptomorin.xseries.messages.ActionBar;
import com.eintosti.elections.api.election.settings.SettingsPhase;
import com.eintosti.elections.election.ElectionImpl;
import com.eintosti.elections.election.ElectionSettings;
import com.eintosti.elections.util.external.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * A phase which has a countdown.
 *
 * @author Trichtern
 */
public abstract class CountdownPhase extends AbstractPhase {

    private final ElectionImpl election;
    private final ElectionSettings settings;
    private final SettingsPhase phase;

    private BukkitTask countdownTask;
    private int countdown;

    public CountdownPhase(ElectionImpl election, SettingsPhase phase) {
        this.election = election;
        this.settings = election.getSettings();
        this.phase = phase;

        this.countdown = settings.getCountdown(phase);
    }

    @Override
    public abstract AbstractPhase getNextPhase();

    @Override
    public SettingsPhase getSettingsPhase() {
        return phase;
    }

    public int getRemainingTime() {
        return countdown;
    }

    @Override
    public void onStart() {
        sendActionBarTimer();

        this.countdownTask = Bukkit.getScheduler().runTaskTimer(election.getPlugin(), () -> {
            this.countdown--;
            sendActionBarTimer();

            if (countdown <= 0) {
                countdownTask.cancel();
                election.startNextPhase();
            }
        }, 20L, 20L);
    }

    private void sendActionBarTimer() {
        if (settings.isActionbar(phase)) {
            Bukkit.getOnlinePlayers().forEach(pl -> ActionBar.sendActionBar(pl, "§a" + StringUtils.formatTime(countdown)));
        }
    }

    @Override
    public void onFinish() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        Bukkit.getOnlinePlayers().forEach(pl -> ActionBar.sendActionBar(pl, ""));
    }
}
