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
package de.eintosti.elections.inventory.listener;

import com.cryptomorin.xseries.XSound;
import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.candidate.Candidate;
import de.eintosti.elections.api.election.settings.Settings;
import de.eintosti.elections.election.ElectionImpl;
import de.eintosti.elections.util.InventoryUtils;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.AnvilGUI.ResponseAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map.Entry;

public class NominationListener implements Listener {

    private final ElectionImpl election;
    private final Settings settings;

    public NominationListener(ElectionsPlugin plugin) {
        this.election = plugin.getElection();
        this.settings = election.getSettings();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!InventoryUtils.isValidClick(event, MessagesOld.getString("nominate_title"))) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Candidate candidate = election.getOrCreateCandidate(player);
        if (!player.hasPermission("elections.run")) {
            return;
        }

        switch (event.getSlot()) {
            case 10:
                if (!election.isNominated(candidate.getUniqueId())) {
                    player.closeInventory();
                    MessagesOld.sendMessage(player, "nomination_status_notNominated");
                    XSound.ENTITY_ITEM_BREAK.play(player);
                    return;
                }
                XSound.ENTITY_ITEM_PICKUP.play(player);
                openStatusAnvil(player, candidate);
                break;

            case 11:
                if (candidate.getStatus() != null) {
                    candidate.setStatus(null);
                    player.closeInventory();
                    MessagesOld.sendMessage(player, "nomination_status_resetStatus");
                }
                break;

            case 16:
                Entry<String, Object> placeholder = new SimpleEntry<>("%position%", settings.position().get());
                if (election.isNominated(candidate.getUniqueId())) {
                    election.removeNomination(candidate);
                    MessagesOld.sendMessage(player, "run_stopRunning", placeholder);
                } else {
                    election.addNomination(candidate);
                    MessagesOld.sendMessage(player, "run_startRunning", placeholder);
                }
                player.closeInventory();
                XSound.ENTITY_CHICKEN_EGG.play(player);
                break;
        }
    }

    private void openStatusAnvil(Player anvilPlayer, Candidate candidate) {
        int maxLength = settings.maxStatusLength().get();
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    String status = stateSnapshot.getText().trim();
                    if (status.length() > maxLength) {
                        status = status.substring(0, maxLength);
                    }
                    candidate.setStatus(status);

                    Player player = stateSnapshot.getPlayer();
                    XSound.ENTITY_PLAYER_LEVELUP.play(player);
                    MessagesOld.sendMessage(player, "nomination_status_statusSet", new SimpleEntry<>("%status%", status));
                    return Collections.singletonList(ResponseAction.close());
                })
                .text("Max. length: " + maxLength)
                .plugin(election.getPlugin())
                .open(anvilPlayer);
    }
}