package com.eintosti.elections.election.phase;

import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.phase.Phase;
import com.eintosti.elections.api.election.settings.SettingsPhase;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractPhase implements Phase {

    @Override
    public abstract AbstractPhase getNextPhase();

    @Override
    public abstract SettingsPhase getSettingsPhase();

    @Override
    public abstract void onStart();

    public void start() {
        Bukkit.getServer().getPluginManager().registerEvents(this, JavaPlugin.getPlugin(ElectionsPlugin.class));
        this.onStart();
    }

    @Override
    public abstract void onFinish();

    public void finish() {
        HandlerList.unregisterAll(this);
        this.onFinish();
    }
}
