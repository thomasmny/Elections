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
import com.cryptomorin.xseries.profiles.objects.Profileable;
import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.inventory.listener.TimeListener;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.InventoryUtils;
import de.eintosti.elections.util.external.StringUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TimeInventory implements InventoryHolder {

    private final ElectionsPlugin plugin;
    private final Inventory inventory;

    public TimeInventory(ElectionsPlugin plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 27, Messages.getString("time.title"));

        Bukkit.getPluginManager().registerEvents(new TimeListener(plugin), plugin);
    }

    public Inventory getInventory(PhaseType phase) {
        addBorder();
        addButtons();

        String phaseKey = phase.name().toLowerCase();
        InventoryUtils.addItemStack(
                inventory, 13, XMaterial.CLOCK,
                Messages.getString("time.phase.title." + phaseKey),
                Messages.getStringList("time.phase.lore." + phaseKey,
                        Placeholder.unparsed("time",
                                StringUtils.formatTime(plugin.getElection().getSettings().countdown(phase).get())
                        )
                )
        );

        return inventory;
    }

    private void addBorder() {
        for (int i = 0; i <= 9; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
        for (int i = 17; i <= 26; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
    }

    private void addButtons() {
        InventoryUtils.addSkull(inventory, 10, "§c- §724:00:00", Profileable.detect("a8c67fed7a2472b7e9afd8d772c13db7b82c32ceeff8db977474c11e4611"));
        InventoryUtils.addSkull(inventory, 11, "§c- §701:00:00", Profileable.detect("a8c67fed7a2472b7e9afd8d772c13db7b82c32ceeff8db977474c11e4611"));
        InventoryUtils.addSkull(inventory, 12, "§c- §700:01:00", Profileable.detect("a8c67fed7a2472b7e9afd8d772c13db7b82c32ceeff8db977474c11e4611"));

        InventoryUtils.addSkull(inventory, 14, "§a+ §700:01:00", Profileable.detect("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716"));
        InventoryUtils.addSkull(inventory, 15, "§a+ §701:00:00", Profileable.detect("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716"));
        InventoryUtils.addSkull(inventory, 16, "§a+ §724:00:00", Profileable.detect("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716"));
    }

    @Internal
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}