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

import com.cryptomorin.xseries.messages.ActionBar;
import de.eintosti.elections.api.election.phase.Phase;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.election.ElectionImpl;
import de.eintosti.elections.election.ElectionSettings;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.external.StringUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

/**
 * A {@link Phase} which has a countdown.
 */
@NullMarked
public abstract class CountdownPhase extends AbstractPhase {

    private final ElectionImpl election;
    private final ElectionSettings settings;
    private final PhaseType phase;

    @Nullable
    private BukkitTask countdownTask;
    private int countdown;

    public CountdownPhase(ElectionImpl election, PhaseType phase) {
        this.election = election;
        this.settings = election.getSettings();
        this.phase = phase;

        this.countdown = settings.countdown(phase).get();
    }

    @Override
    public PhaseType getPhaseType() {
        return phase;
    }

    @Override
    public abstract AbstractPhase getNextPhase();

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
                if (countdownTask != null) {
                    countdownTask.cancel();
                }
                election.startNextPhase();
            }
        }, 20L, 20L);
    }

    private void sendActionBarTimer() {
        if (!settings.actionBar(phase).get()) {
            return;
        }

        String phaseKey = phase.name().toLowerCase(Locale.ROOT);
        Bukkit.getOnlinePlayers().forEach(pl -> ActionBar.sendActionBar(pl,
                Messages.getString("election." + phaseKey + ".actionbar",
                        Placeholder.unparsed("time", StringUtils.formatTime(countdown))
                ))
        );
    }

    @Override
    public void onFinish() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        Bukkit.getOnlinePlayers().forEach(ActionBar::clearActionBar);
    }
}
