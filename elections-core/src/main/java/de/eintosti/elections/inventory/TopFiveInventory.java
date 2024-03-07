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
package de.eintosti.elections.inventory;

import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.candidate.Candidate;
import de.eintosti.elections.election.ElectionImpl;
import de.eintosti.elections.inventory.listener.TopFiveListener;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Range;

import java.util.List;

public class TopFiveInventory {

    private final ElectionImpl election;

    public TopFiveInventory(ElectionsPlugin plugin) {
        this.election = plugin.getElection();

        Bukkit.getPluginManager().registerEvents(new TopFiveListener(), plugin);
    }

    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 36, Messages.getString("top_5.title"));
        addBorder(inventory);

        List<Candidate> top5 = election.getTopFive();
        if (top5.isEmpty()) {
            return inventory;
        }

        int slot = 11;
        for (int rank = 1; rank <= top5.size(); rank++) {
            Candidate candidate = top5.get(rank - 1);
            InventoryUtils.addSkull(inventory, slot, "§a" + candidate.getName(), candidate.getName());
            addPositionSkull(inventory, slot + 9, rank);
            slot++;
        }

        return inventory;
    }

    private void addPositionSkull(Inventory inventory, int position, @Range(from = 1, to = 5) int rank) {
        String displayName, url;

        switch (rank) {
            case 1:
                displayName = Messages.getString("top_5.first");
                url = "af3034d24a85da31d67932c33e5f1821e219d5dcd9c2ba4f2559df48deea";
                break;
            case 2:
                displayName = Messages.getString("top_5.second");
                url = "dc61b04e12a879767b3b72d69627f29a83bdeb6220f5dc7bea2eb2529d5b097";
                break;
            case 3:
                displayName = Messages.getString("top_5.third");
                url = "f8ebab57b7614bb22a117be43e848bcd14daecb50e8f5d0926e4864dff470";
                break;
            case 4:
                displayName = Messages.getString("top_5.fourth");
                url = "d2e78fb22424232dc27b81fbcb47fd24c1acf76098753f2d9c28598287db5";
                break;
            case 5:
                displayName = Messages.getString("top_5.fifth");
                url = "6d57e3bc88a65730e31a14e3f41e038a5ecf0891a6c243643b8e5476ae2";
                break;
            default:
                throw new IllegalArgumentException("Unable to set skull for rank " + rank);
        }

        InventoryUtils.addSkull(inventory, position, displayName, url);
    }

    private void addBorder(Inventory inventory) {
        for (int i = 0; i <= 10; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
        for (int i = 16; i <= 19; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
        for (int i = 25; i <= 35; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
    }
}