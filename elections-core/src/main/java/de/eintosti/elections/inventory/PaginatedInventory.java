package com.eintosti.elections.inventory;

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