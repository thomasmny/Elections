package com.eintosti.elections.inventory.listener;

import com.cryptomorin.xseries.XSound;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.settings.SettingsPhase;
import com.eintosti.elections.election.ElectionSettings;
import com.eintosti.elections.inventory.CreateInventory.Page;
import com.eintosti.elections.util.InventoryUtils;
import com.eintosti.elections.util.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TimeListener implements Listener {

    private static final int PHASE_INFORMATION = 13;

    private final ElectionsPlugin plugin;
    private final ElectionSettings settings;

    public TimeListener(ElectionsPlugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getElection().getSettings();
    }

    @EventHandler
    public void onInventoryClickTime(InventoryClickEvent event) {
        if (!InventoryUtils.checkIfValidClick(event, Messages.getString("time_title"))) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String displayName = event.getInventory().getItem(PHASE_INFORMATION).getItemMeta().getDisplayName();
        SettingsPhase phase = displayName.equals(Messages.getString("time_nomination")) ? SettingsPhase.NOMINATION : SettingsPhase.VOTING;

        switch (event.getSlot()) {
            case 10:
                checkIfCountdownReducible(phase, player, 86400);
                break;
            case 11:
                checkIfCountdownReducible(phase, player, 3600);
                break;
            case 12:
                checkIfCountdownReducible(phase, player, 60);
                break;
            case 13:
                Page page = phase == SettingsPhase.NOMINATION ? Page.NOMINATION : Page.VOTING;
                player.openInventory(plugin.getCreateInventory().getInventory(page));
                break;
            case 14:
                modifyCountdown(phase, player, 60);
                break;
            case 15:
                modifyCountdown(phase, player, 3600);
                break;
            case 16:
                modifyCountdown(phase, player, 86400);
                break;
            default:
                return;
        }

        XSound.ENTITY_ITEM_PICKUP.play(player);
    }

    private void checkIfCountdownReducible(SettingsPhase phase, Player player, int amount) {
        if ((settings.getCountdown(phase) - amount) > 0) {
            XSound.ENTITY_ITEM_PICKUP.play(player);
            modifyCountdown(phase, player, -amount);
        } else {
            XSound.ENTITY_ITEM_BREAK.play(player);
            player.sendMessage(Messages.getString("timer_less0"));
        }
    }

    private void modifyCountdown(SettingsPhase phase, Player player, int amount) {
        settings.setCountdown(phase, settings.getCountdown(phase) + amount);
        XSound.ENTITY_ITEM_PICKUP.play(player);
        player.openInventory(plugin.getTimeInventory().getInventory(phase));
    }
}