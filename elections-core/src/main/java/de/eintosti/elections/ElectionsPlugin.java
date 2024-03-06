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
package de.eintosti.elections;

import de.eintosti.elections.api.Elections;
import de.eintosti.elections.api.ElectionsApi;
import de.eintosti.elections.command.ElectionsCommand;
import de.eintosti.elections.election.ElectionImpl;
import de.eintosti.elections.inventory.CreationInventory;
import de.eintosti.elections.inventory.RunInventory;
import de.eintosti.elections.inventory.TimeInventory;
import de.eintosti.elections.inventory.TopFiveInventory;
import de.eintosti.elections.inventory.VoteInventory;
import de.eintosti.elections.messages.MessagesProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ElectionsPlugin extends JavaPlugin {

    private ElectionImpl election;

    private BukkitAudiences adventure;

    private CreationInventory creationInventory;
    private RunInventory runInventory;
    private TimeInventory timeInventory;
    private VoteInventory votingInventory;
    private TopFiveInventory topFiveInventory;

    @Override
    public void onLoad() {
        new MessagesProvider(this).setup();
    }

    @Override
    public void onEnable() {
        Elections api = new ElectionsApi();
        getServer().getServicesManager().register(Elections.class, api, this, ServicePriority.Normal);
        ElectionsApi.register(api);

        this.election = new ElectionImpl(this);

        this.creationInventory = new CreationInventory(this);
        this.runInventory = new RunInventory(this);
        this.timeInventory = new TimeInventory(this);
        this.votingInventory = new VoteInventory(this);
        this.topFiveInventory = new TopFiveInventory(this);

        new ElectionsCommand(this);
    }

    @Override
    public void onDisable() {
        this.election.getPhase().finish();

        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }

        ElectionsApi.unregister();
    }

    public @NotNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public void resetElection() {
        this.election.getPhase().finish();
        this.election = new ElectionImpl(this);
    }

    public ElectionImpl getElection() {
        return election;
    }

    public CreationInventory getCreationInventory() {
        return creationInventory;
    }

    public RunInventory getRunInventory() {
        return runInventory;
    }

    public TimeInventory getTimeInventory() {
        return timeInventory;
    }

    public VoteInventory getVoteInventory() {
        return votingInventory;
    }

    public TopFiveInventory getTopInventory() {
        return topFiveInventory;
    }
}