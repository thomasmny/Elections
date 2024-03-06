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

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PaginatedInventory {

    protected final Map<UUID, Integer> invIndex;
    protected Inventory[] inventories;

    public PaginatedInventory() {
        this.invIndex = new HashMap<>();
    }

    public int getInvIndex(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (!invIndex.containsKey(playerUUID)) {
            resetInvIndex(player);
        }
        return invIndex.get(playerUUID);
    }

    public void resetInvIndex(Player player) {
        invIndex.put(player.getUniqueId(), 0);
    }

    public void nextPage(Player player) {
        invIndex.put(player.getUniqueId(), getInvIndex(player) + 1);
    }

    public void previousPage(Player player) {
        invIndex.put(player.getUniqueId(), getInvIndex(player) - 1);
    }
}