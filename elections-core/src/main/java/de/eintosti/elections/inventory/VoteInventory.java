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
import de.eintosti.elections.inventory.listener.VoteListener;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.InventoryUtils;
import de.eintosti.elections.util.external.StringUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@NullMarked
public class VoteInventory extends PaginatedInventory {

    private static final int MAX_CANDIDATES = 9;

    private final ElectionImpl election;
    private final Settings settings;

    @Nullable
    private BukkitTask timeLeftTask;
    private int numCandidates = 0;

    public VoteInventory(ElectionsPlugin plugin) {
        this.election = plugin.getElection();
        this.settings = election.getSettings();

        Bukkit.getPluginManager().registerEvents(new VoteListener(plugin), plugin);
    }

    private Inventory createInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, Messages.getString("vote.title"));
        addBorder(inventory, player);
        return inventory;
    }

    public Inventory getInventory(Player player) {
        addCandidates(player);
        return inventories[getInvIndex(player)];
    }

    private void addCandidates(Player player) {
        List<Candidate> candidates = new ArrayList<>(election.getCandidates());
        candidates.sort(Comparator.comparing(candidate -> candidate.getName().toLowerCase()));
        this.numCandidates = candidates.size();
        int numInventories = (numCandidates % MAX_CANDIDATES == 0 ? numCandidates : numCandidates + 1) != 0 ? (numCandidates % MAX_CANDIDATES == 0 ? numCandidates : numCandidates + 1) : 1;

        int index = 0;

        Inventory inventory = createInventory(player);

        inventories = new Inventory[numInventories];
        inventories[index] = inventory;

        int columnSkull = 9, maxColumnSkull = 17;
        for (Candidate candidate : candidates) {
            addCandidateInformation(player, candidate, inventory, columnSkull++);

            if (columnSkull > maxColumnSkull) {
                columnSkull = 9;
                inventory = createInventory(player);
                inventories[++index] = inventory;
            }
        }
    }

    private void addCandidateInformation(Player player, Candidate candidate, Inventory inventory, int position) {
        String displayName = Messages.getString("vote.player.notvoted");
        String status = candidate.hasStatus() ? "§a§o\"" + candidate.getStatus() + "\"" : "";
        XMaterial material = XMaterial.GRAY_DYE;
        String prefix = "§7";

        if (election.hasVotedFor(player, candidate)) {
            displayName = Messages.getString("vote.player.voted");
            material = XMaterial.LIME_DYE;
            prefix = "§a";
        }

        InventoryUtils.addSkull(inventory, position, prefix + candidate.getName(), candidate.getName(), status);
        InventoryUtils.addItemStack(inventory, position + 9, material, displayName);
    }

    private void addBorder(Inventory inventory, Player player) {
        for (int i = 0; i <= 9; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }

        if (!settings.scoreboard(PhaseType.VOTING).get()) {
            this.timeLeftTask = Bukkit.getScheduler().runTaskTimer(election.getPlugin(), () -> {
                if (settings.countdown(PhaseType.VOTING).get() > 0) {
                    addTimeLeftItem(inventory);
                } else if (timeLeftTask != null) {
                    timeLeftTask.cancel();
                }
            }, 0L, 5L);
        }

        for (int i = 27; i <= 35; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }

        int numOfPages = (numCandidates / MAX_CANDIDATES) + (numCandidates % MAX_CANDIDATES == 0 ? 0 : 1);
        int invIndex = getInvIndex(player);

        if (numOfPages > 1 && invIndex > 0) {
            InventoryUtils.addSkull(inventory, 27, Messages.getString("vote.page.previous"),
                    "f7aacad193e2226971ed95302dba433438be4644fbab5ebf818054061667fbe2"
            );
        } else {
            InventoryUtils.addGlassPane(inventory, 27);
        }

        if (numOfPages > 1 && invIndex < (numOfPages - 1)) {
            InventoryUtils.addSkull(inventory, 35, Messages.getString("vote.page.next"),
                    "d34ef0638537222b20f480694dadc0f85fbe0759d581aa7fcdf2e43139377158"
            );
        } else {
            InventoryUtils.addGlassPane(inventory, 35);
        }
    }

    private void addTimeLeftItem(Inventory inventory) {
        ItemStack itemStack = XMaterial.BOOK.parseItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Messages.getString("vote.time.remaining.title"));

        List<String> lore = new ArrayList<>();
        int countdown = settings.countdown(PhaseType.NOMINATION).get();
        if (countdown > 0) {
            lore.add(Messages.getString("vote.time.remaining.duration",
                    Placeholder.unparsed("duration", StringUtils.formatTime(countdown))
            ));
        } else {
            lore.add(Messages.getString("vote.time.remaining.finished"));
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(4, itemStack);
    }
}