package com.eintosti.elections.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.settings.Settings;
import com.eintosti.elections.api.election.settings.SettingsPhase;
import com.eintosti.elections.inventory.listener.TimeListener;
import com.eintosti.elections.util.InventoryUtils;
import com.eintosti.elections.util.Messages;
import com.eintosti.elections.util.external.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.AbstractMap.SimpleEntry;

public class TimeInventory {

    private final Settings settings;

    public TimeInventory(ElectionsPlugin plugin) {
        this.settings = plugin.getElection().getSettings();

        Bukkit.getPluginManager().registerEvents(new TimeListener(plugin), plugin);
    }

    public Inventory getInventory(SettingsPhase phase) {
        Inventory inventory = Bukkit.createInventory(null, 27, Messages.getString("time_title"));
        fillGuiWithGlass(inventory);
        addButtons(inventory);

        String key = phase.name().toLowerCase();
        InventoryUtils.addItemStack(
                inventory, 13, XMaterial.CLOCK, Messages.getString("time_" + key),
                Messages.getStringList("time_" + key + "_lore", new SimpleEntry<>("%time%", StringUtils.formatTime(settings.getCountdown(phase))))
        );

        return inventory;
    }

    private void fillGuiWithGlass(Inventory inventory) {
        for (int i = 0; i <= 9; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
        for (int i = 17; i <= 26; i++) {
            InventoryUtils.addGlassPane(inventory, i);
        }
    }

    private void addButtons(Inventory inventory) {
        InventoryUtils.addUrlSkull(inventory, 10, "§c- §724:00:00", "a8c67fed7a2472b7e9afd8d772c13db7b82c32ceeff8db977474c11e4611");
        InventoryUtils.addUrlSkull(inventory, 11, "§c- §701:00:00", "a8c67fed7a2472b7e9afd8d772c13db7b82c32ceeff8db977474c11e4611");
        InventoryUtils.addUrlSkull(inventory, 12, "§c- §700:01:00", "a8c67fed7a2472b7e9afd8d772c13db7b82c32ceeff8db977474c11e4611");

        InventoryUtils.addUrlSkull(inventory, 14, "§a+ §700:01:00", "3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716");
        InventoryUtils.addUrlSkull(inventory, 15, "§a+ §701:00:00", "3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716");
        InventoryUtils.addUrlSkull(inventory, 16, "§a+ §724:00:00", "3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716");
    }
}