package com.eintosti.elections.election.phase;

import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.settings.SettingsPhase;

public class SetupPhase extends AbstractPhase {

    private final ElectionsPlugin plugin;

    public SetupPhase(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public AbstractPhase getNextPhase() {
        return new NominationPhase(plugin);
    }

    @Override
    public SettingsPhase getSettingsPhase() {
        return SettingsPhase.SETUP;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onFinish() {
    }
}