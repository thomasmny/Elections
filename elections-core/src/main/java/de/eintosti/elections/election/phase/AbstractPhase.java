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

import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.phase.Phase;
import de.eintosti.elections.api.election.phase.PhaseType;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is an abstract implementation of the Phase interface.
 * It provides default implementations for some of the methods and defines abstract methods that must be implemented by subclasses.
 */
public abstract class AbstractPhase implements Phase {

    @Override
    public abstract PhaseType getPhaseType();

    @Override
    public abstract AbstractPhase getNextPhase();

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
