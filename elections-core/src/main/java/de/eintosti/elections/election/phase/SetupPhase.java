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
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.election.Election;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SetupPhase extends AbstractPhase {

    private final ElectionsPlugin plugin;
    private final Election election;

    public SetupPhase(ElectionsPlugin plugin, Election election) {
        this.plugin = plugin;
        this.election = election;
    }

    @Override
    public PhaseType getPhaseType() {
        return PhaseType.SETUP;
    }

    @Override
    public AbstractPhase getNextPhase() {
        return new NominationPhase(plugin, election);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onFinish() {
    }
}