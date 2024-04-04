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
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.election.ElectionImpl;
import de.eintosti.elections.inventory.VoteInventory;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class VoteListener implements Listener {

    private final ElectionsPlugin plugin;

    public VoteListener(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!InventoryUtils.isValidClick(event, Messages.getString("vote.title"))) {
            return;
        }

        ElectionImpl election = plugin.getElection();
        if (election.getCurrentPhase().getPhaseType() != PhaseType.VOTING) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("elections.vote")) {
            player.closeInventory();
            Messages.sendMessage(player, "election.vote.no_permission");
            return;
        }

        VoteInventory voteInventory = plugin.getVoteInventory();

        switch (XMaterial.matchXMaterial(event.getCurrentItem())) {
            case LIME_DYE:
            case GRAY_DYE:
                int skullSlot = event.getSlot() - 9;
                if (skullSlot >= 9 && skullSlot <= 17) {
                    ItemStack skull = event.getView().getItem(skullSlot);
                    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                    String ownerName = skullMeta.getOwner();
                    election.voteFor(player, election.getCandidate(ownerName));
                    XSound.ENTITY_CHICKEN_EGG.play(player);
                    player.openInventory(voteInventory.getInventory(player));
                }
                break;

            case PLAYER_HEAD:
                switch (event.getSlot()) {
                    case 27:
                        voteInventory.previousPage(player);
                        break;
                    case 35:
                        voteInventory.nextPage(player);
                        break;
                    default:
                        return;
                }
                player.openInventory(voteInventory.getInventory(player));
                break;
        }
    }
}
