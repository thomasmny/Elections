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
package de.eintosti.elections.util;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;

@NullMarked
public final class InventoryUtils {

    private InventoryUtils() {
        throw new IllegalStateException("This is a utility class");
    }

    public static ItemStack getItemStack(XMaterial material, String displayName, List<String> lore) {
        ItemStack itemStack = material.parseItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setLore(lore);
        itemMeta.setUnbreakable(true);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void addItemStack(Inventory inventory, int position, XMaterial material, String displayName, List<String> lore) {
        ItemStack itemStack = getItemStack(material, displayName, lore);
        inventory.setItem(position, itemStack);
    }

    public static void addItemStack(Inventory inventory, int position, XMaterial material, String displayName, String... lore) {
        addItemStack(inventory, position, material, displayName, Arrays.asList(lore));
    }

    public static void addEnchantedItemStack(Inventory inventory, int position, XMaterial material, int amount, String displayName, boolean enchant, List<String> lore) {
        ItemStack itemStack = getItemStack(material, displayName, lore);
        itemStack.setAmount(amount);
        if (enchant) {
            itemStack.addUnsafeEnchantment(XEnchantment.UNBREAKING.getEnchant(), 1);
        }
        inventory.setItem(position, itemStack);
    }

    public static void addGlassPane(Inventory inventory, int position) {
        addItemStack(inventory, position, XMaterial.BLACK_STAINED_GLASS_PANE, "§7");
    }

    public static ItemStack getSkull(String displayName, Profileable profile, List<String> lore) {
        ItemStack skull = XSkull.createItem()
                .profile(profile)
                .apply();
        ItemMeta itemMeta = skull.getItemMeta();

        itemMeta.setDisplayName(displayName);
        if (!lore.isEmpty()) {
            itemMeta.setLore(lore);
        }
        itemMeta.addItemFlags(ItemFlag.values());
        skull.setItemMeta(itemMeta);

        return skull;
    }

    public static ItemStack getSkull(String displayName, Profileable profile, String... lore) {
        return getSkull(displayName, profile, Arrays.asList(lore));
    }

    public static void addSkull(Inventory inventory, int position, String displayName, Profileable profile, String... lore) {
        inventory.setItem(position, getSkull(displayName, profile, lore));
    }

    public static void addSkull(Inventory inventory, int position, String displayName, Profileable profile, List<String> lore) {
        inventory.setItem(position, getSkull(displayName, profile, lore));
    }
}