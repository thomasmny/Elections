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

import com.cryptomorin.xseries.XMaterial;
import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.candidate.Candidate;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.api.election.settings.Settings;
import de.eintosti.elections.election.ElectionImpl;
import de.eintosti.elections.inventory.listener.NominationListener;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.InventoryUtils;
import de.eintosti.elections.util.external.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class NominationInventory {

    private final ElectionImpl election;
    private final Settings settings;

    private BukkitTask timeLeftTask;

    public NominationInventory(ElectionsPlugin plugin) {
        this.election = plugin.getElection();
        this.settings = election.getSettings();

        Bukkit.getPluginManager().registerEvents(new NominationListener(plugin), plugin);
    }

    public Inventory getInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, Messages.getString("nomination.title"));
        Candidate candidate = election.getOrCreateCandidate(player);
        addBorder(inventory);

        addStatusItemStack(candidate, inventory);
        InventoryUtils.addItemStack(inventory, 11, XMaterial.ANVIL, Messages.getString("nominate_resetStatus_name"), MessagesOld.getStringList("nominate_resetStatus_lore"));

        addCurrentStatus(candidate, inventory);
        addElectionRunItem(candidate, inventory);

        return inventory;
    }

    private void addStatusItemStack(Candidate candidate, Inventory inventory) {
        String displayName = Messages.getString("nominate_cannotSetStatus_name");
        List<String> lore = Messages.getStringList("nominate_cannotSetStatus_lore");

        if (election.isNominated(candidate.getUniqueId())) {
            displayName = Messages.getString("nominate_canSetStatus_name");
            lore = Messages.getStringList("nominate_canSetStatus_lore");
        }

        InventoryUtils.addItemStack(inventory, 10, XMaterial.WRITABLE_BOOK, displayName, lore);
    }

    private void addCurrentStatus(Candidate candidate, Inventory inv) {
        String status = candidate.getStatus();
        String formattedStatus = status == null ? " §c-" : " §a§o" + status;

        InventoryUtils.addItemStack(inv, 13, XMaterial.NAME_TAG, Messages.getString("nominate_currentStatus_name") + formattedStatus);
    }

    private void addElectionRunItem(Candidate candidate, Inventory inv) {
        Entry<String, Object> placeholder = new SimpleEntry<>("%position%", settings.position().get());

        String displayName = Messages.getString("nominate_running_name", placeholder);
        List<String> lore = Messages.getStringList("nominate_running_lore", placeholder);
        XMaterial material = XMaterial.LIME_DYE;

        if (!election.isNominated(candidate.getUniqueId())) {
            displayName = Messages.getString("nominate_notRunning_name", placeholder);
            lore = Messages.getStringList("nominate_notRunning_lore", placeholder);
            material = XMaterial.RED_DYE;
        }

        InventoryUtils.addItemStack(inv, 16, material, displayName, lore);
    }

    private void addBorder(Inventory inventory) {
        for (int i = 0; i <= 9; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }

        if (!settings.scoreboard(PhaseType.NOMINATION).get()) {
            this.timeLeftTask = Bukkit.getScheduler().runTaskTimer(election.getPlugin(), () -> {
                if (settings.countdown(PhaseType.NOMINATION).get() > 0) {
                    addTimeLeftItem(inventory);
                } else {
                    this.timeLeftTask.cancel();
                }
            }, 0L, 5L);
        }

        for (int i = 17; i <= 26; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
    }

    private void addTimeLeftItem(Inventory inventory) {
        ItemStack itemStack = XMaterial.BOOK.parseItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Messages.getString("nominate_time_remaining"));

        List<String> lore = new ArrayList<>();
        int countdown = settings.countdown(PhaseType.NOMINATION).get();
        if (countdown > 0) {
            lore.add(" §a" + StringUtils.formatTime(countdown));
        } else {
            lore.add(Messages.getString("nominate_time_finished"));
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(4, itemStack);
    }
}