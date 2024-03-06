package com.eintosti.elections.api.election.phase;

import com.eintosti.elections.api.election.settings.SettingsPhase;
import org.bukkit.event.Listener;

public interface Phase extends Listener {

    /**
     * Gets the {@link Phase} which will succeed the current one.
     *
     * @return The phase that will be run after the current one
     */
    Phase getNextPhase();

    /**
     * Gets the {@link SettingsPhase} linked to this phase.
     *
     * @return The setting phase linked to this phase.
     */
    SettingsPhase getSettingsPhase();

    /**
     * Logic which will be run whenever the phase begins.
     */
    void onStart();

    /**
     * Logic which will be run whenever the phase end.
     */
    void onFinish();
}