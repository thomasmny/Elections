package com.eintosti.elections.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.candidate.Candidate;
import com.eintosti.elections.api.election.settings.SettingsPhase;
import com.eintosti.elections.election.ElectionImpl;
import com.eintosti.elections.election.ElectionSettings;
import com.eintosti.elections.inventory.listener.VoteListener;
import com.eintosti.elections.util.InventoryUtils;
import com.eintosti.elections.util.Messages;
import com.eintosti.elections.util.external.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VoteInventory extends PaginatedInventory {

    private static final int MAX_CANDIDATES = 9;

    private final ElectionImpl election;
    private final ElectionSettings settings;

    private BukkitTask timeLeftTask;
    private int numCandidates = 0;

    public VoteInventory(ElectionsPlugin plugin) {
        this.election = plugin.getElection();
        this.settings = election.getSettings();

        Bukkit.getPluginManager().registerEvents(new VoteListener(plugin), plugin);
    }

    private Inventory createInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, Messages.getString("vote_title"));
        fillGuiWithGlass(inventory, player);
        return inventory;
    }

    public Inventory getInventory(Player player) {
        addCandidates(player);
        return inventories[getInvIndex(player)];
    }

    private void addCandidates(Player player) {
        List<Candidate> candidates = new ArrayList<>(election.getNominations().values());
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
        String displayName = Messages.getString("vote_player_notVoted");
        List<String> status = candidate.hasStatus() ? Collections.singletonList("§a§o\"" + candidate.getStatus() + "\"") : new ArrayList<>();
        XMaterial material = XMaterial.GRAY_DYE;
        String prefix = "§7";

        if (election.hasVoted(player, candidate)) {
            displayName = Messages.getString("vote_player_voted");
            material = XMaterial.LIME_DYE;
            prefix = "§a";
        }

        InventoryUtils.addSkull(inventory, position, prefix + candidate.getName(), candidate.getName(), status);
        InventoryUtils.addItemStack(inventory, position + 9, material, displayName);
    }

    private void fillGuiWithGlass(Inventory inventory, Player player) {
        for (int i = 0; i <= 9; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }

        if (!settings.isScoreboard(SettingsPhase.VOTING)) {
            this.timeLeftTask = Bukkit.getScheduler().runTaskTimer(election.getPlugin(), () -> {
                if (settings.getCountdown(SettingsPhase.VOTING) > 0) {
                    addTimeLeftItem(inventory);
                } else {
                    this.timeLeftTask.cancel();
                }
            }, 0L, 5L);
        }

        for (int i = 27; i <= 35; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }

        int numOfPages = (numCandidates / MAX_CANDIDATES) + (numCandidates % MAX_CANDIDATES == 0 ? 0 : 1);
        int invIndex = getInvIndex(player);

        if (numOfPages > 1 && invIndex > 0) {
            InventoryUtils.addUrlSkull(inventory, 27, Messages.getString("vote_previousPage"), "f7aacad193e2226971ed95302dba433438be4644fbab5ebf818054061667fbe2");
        } else {
            InventoryUtils.addGlassPane(inventory, 27);
        }

        if (numOfPages > 1 && invIndex < (numOfPages - 1)) {
            InventoryUtils.addUrlSkull(inventory, 35, Messages.getString("vote_nextPage"), "d34ef0638537222b20f480694dadc0f85fbe0759d581aa7fcdf2e43139377158");
        } else {
            InventoryUtils.addGlassPane(inventory, 35);
        }
    }

    private void addTimeLeftItem(Inventory inventory) {
        ItemStack itemStack = XMaterial.BOOK.parseItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Messages.getString("vote_time_remaining"));

        List<String> lore = new ArrayList<>();
        int countdown = settings.getCountdown(SettingsPhase.VOTING);
        if (countdown > 0) {
            lore.add(" §a" + StringUtils.formatTime(countdown));
        } else {
            lore.add(Messages.getString("vote_time_finished"));
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(4, itemStack);
    }
}