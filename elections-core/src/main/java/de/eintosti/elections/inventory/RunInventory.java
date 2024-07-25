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
import de.eintosti.elections.api.election.Election;
import de.eintosti.elections.api.election.candidate.Candidate;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.api.election.settings.Settings;
import de.eintosti.elections.inventory.listener.RunListener;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.InventoryUtils;
import de.eintosti.elections.util.external.StringUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class RunInventory implements InventoryHolder {

    private final ElectionsPlugin plugin;
    private final Inventory inventory;

    @Nullable
    private BukkitTask timeLeftTask;

    public RunInventory(ElectionsPlugin plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 27, Messages.getString("run.title"));

        Bukkit.getPluginManager().registerEvents(new RunListener(plugin), plugin);
    }

    public Inventory getInventory(Player player) {
        addBorder();

        Candidate candidate = plugin.getElection().getOrCreateCandidate(player);
        addStatusItem(candidate);
        addResetStatusItem();
        addCurrentStatus(candidate);
        addElectionRunItem(candidate);

        return inventory;
    }

    private void addStatusItem(Candidate candidate) {
        String key = plugin.getElection().isNominated(candidate.getUniqueId()) ? "enabled" : "disabled";
        InventoryUtils.addItemStack(inventory, 10, XMaterial.WRITABLE_BOOK,
                Messages.getString("run.status." + key + ".title"),
                Messages.getStringList("run.status." + key + ".lore")
        );
    }

    private void addResetStatusItem() {
        InventoryUtils.addItemStack(inventory, 11, XMaterial.ANVIL,
                Messages.getString("run.status.reset.title"), Messages.getStringList("run.status.reset.lore")
        );
    }

    private void addCurrentStatus(Candidate candidate) {
        String status = candidate.getStatus();
        String formattedStatus = status == null ? "§c-" : "§a§o" + status;
        InventoryUtils.addItemStack(inventory, 13, XMaterial.NAME_TAG,
                Messages.getString("run.status.current", Placeholder.unparsed("status", formattedStatus))
        );
    }

    private void addElectionRunItem(Candidate candidate) {
        Election election = plugin.getElection();
        boolean nominated = election.isNominated(candidate.getUniqueId());
        String key = nominated ? "running" : "notrunning";
        TagResolver position = Placeholder.unparsed("position", election.getSettings().position().get());

        InventoryUtils.addItemStack(inventory, 16,
                nominated ? XMaterial.LIME_DYE : XMaterial.RED_DYE,
                Messages.getString("run." + key + ".title", position),
                Messages.getStringList("run." + key + ".lore", position)
        );
    }

    private void addBorder() {
        for (int i = 0; i <= 9; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }

        Settings settings = plugin.getElection().getSettings();
        if (!settings.scoreboard(PhaseType.NOMINATION).get()) {
            this.timeLeftTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (settings.countdown(PhaseType.NOMINATION).get() > 0) {
                    addTimeLeftItem();
                } else if (timeLeftTask != null) {
                    timeLeftTask.cancel();
                }
            }, 0L, 5L);
        }

        for (int i = 17; i <= 26; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
    }

    private void addTimeLeftItem() {
        ItemStack itemStack = XMaterial.BOOK.parseItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Messages.getString("run.time.remaining.title"));

        List<String> lore = new ArrayList<>();
        int countdown = plugin.getElection().getSettings().countdown(PhaseType.NOMINATION).get();
        if (countdown > 0) {
            lore.add(Messages.getString("run.time.remaining.duration",
                    Placeholder.unparsed("duration", StringUtils.formatTime(countdown))
            ));
        } else {
            lore.add(Messages.getString("run.time.remaining.finished"));
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(4, itemStack);
    }

    @Internal
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}