package com.eintosti.elections.inventory.listener;

import com.eintosti.elections.util.InventoryUtils;
import com.eintosti.elections.util.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TopFiveListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryUtils.checkIfValidClick(event, Messages.getString("top5_title"));
    }
}