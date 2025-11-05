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

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.candidate.Candidate;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.election.ElectionImpl;
import de.eintosti.elections.inventory.RunInventory;
import de.eintosti.elections.messages.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.AnvilGUI.ResponseAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;

@NullMarked
public class RunListener implements Listener {

    private final ElectionsPlugin plugin;

    public RunListener(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof RunInventory)) {
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == XMaterial.AIR.parseMaterial() || !itemStack.hasItemMeta()) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("elections.run")) {
            Messages.sendMessage(player, "election.run.no_permission");
            player.closeInventory();
            return;
        }

        ElectionImpl election = plugin.getElection();
        if (election.getCurrentPhase().getPhaseType() != PhaseType.NOMINATION) {
            Messages.sendMessage(player, "election.nomination.over");
            player.closeInventory();
            return;
        }

        Candidate candidate = election.getOrCreateCandidate(player);
        switch (event.getSlot()) {
            case 10:
                if (!election.isNominated(candidate.getUniqueId())) {
                    player.closeInventory();
                    Messages.sendMessage(player, "election.nomination.status.not_nominated");
                    XSound.ENTITY_ITEM_BREAK.play(player);
                    return;
                }
                XSound.ENTITY_ITEM_PICKUP.play(player);
                openStatusAnvil(election, player, candidate);
                break;

            case 11:
                if (candidate.getStatus() != null) {
                    candidate.setStatus(null);
                    player.closeInventory();
                    Messages.sendMessage(player, "election.nomination.status.reset");
                }
                break;

            case 16:
                TagResolver position = Placeholder.unparsed("position", election.getSettings().position().get());
                if (election.isNominated(candidate.getUniqueId())) {
                    election.withdraw(candidate);
                    Messages.sendMessage(player, "election.run.stop", position);
                } else {
                    election.nominate(candidate);
                    Messages.sendMessage(player, "election.run.start", position);
                }
                player.closeInventory();
                XSound.ENTITY_CHICKEN_EGG.play(player);
                break;
        }
    }

    private void openStatusAnvil(ElectionImpl election, Player anvilPlayer, Candidate candidate) {
        int maxLength = election.getSettings().maxStatusLength().get();
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    String status = stateSnapshot.getText().trim();
                    if (status.length() > maxLength) {
                        status = status.substring(0, maxLength);
                    }
                    candidate.setStatus(status);

                    Player player = stateSnapshot.getPlayer();
                    XSound.ENTITY_PLAYER_LEVELUP.play(player);
                    Messages.sendMessage(player, "election.nomination.status.set",
                            Placeholder.unparsed("status", status)
                    );
                    return Collections.singletonList(ResponseAction.close());
                })
                .text("Max. length: " + maxLength)
                .plugin(election.getPlugin())
                .open(anvilPlayer);
    }
}