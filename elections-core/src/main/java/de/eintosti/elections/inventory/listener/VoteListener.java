package com.eintosti.elections.inventory.listener;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.settings.SettingsPhase;
import com.eintosti.elections.election.ElectionImpl;
import com.eintosti.elections.inventory.VoteInventory;
import com.eintosti.elections.util.InventoryUtils;
import com.eintosti.elections.util.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class VoteListener implements Listener {

    private final ElectionsPlugin plugin;

    public VoteListener(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!InventoryUtils.checkIfValidClick(event, Messages.getString("vote_title"))) {
            return;
        }

        ElectionImpl election = plugin.getElection();
        if (election.getPhase().getSettingsPhase() != SettingsPhase.VOTING) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("elections.vote")) {
            player.closeInventory();
            Messages.sendMessage(player, "vote_noPerms");
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
                    election.vote(player, skullMeta.getOwner());
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
