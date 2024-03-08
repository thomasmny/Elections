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

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
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
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }
        inventory.setItem(position, itemStack);
    }

    public static void addGlassPane(Inventory inventory, int position) {
        addItemStack(inventory, position, XMaterial.BLACK_STAINED_GLASS_PANE, "§7");
    }

    public static ItemStack getSkull(String displayName, String identifier, List<String> lore) {
        ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta skullMeta = SkullUtils.applySkin(skull.getItemMeta(), identifier).clone();

        skullMeta.setDisplayName(displayName);
        if (!lore.isEmpty()) {
            skullMeta.setLore(lore);
        }
        skullMeta.addItemFlags(ItemFlag.values());
        skull.setItemMeta(skullMeta);

        return skull;
    }

    public static ItemStack getSkull(String displayName, String identifier, String... lore) {
        return getSkull(displayName, identifier, Arrays.asList(lore));
    }

    public static void addSkull(Inventory inventory, int position, String displayName, String identifier, String... lore) {
        inventory.setItem(position, getSkull(displayName, identifier, lore));
    }

    public static void addSkull(Inventory inventory, int position, String displayName, String identifier, List<String> lore) {
        inventory.setItem(position, getSkull(displayName, identifier, lore));
    }

    /**
     * Checks whether the clicked {@link Inventory} is equivalent to the inventory with the given name.<br>
     * Also makes sure that the clicked {@link ItemStack} is not {@code null} and if so, cancels the event.
     *
     * @param event         The inventory click event
     * @param inventoryName The name of the inventory to compare with
     * @return {@code true} if the above is true, {@code false} otherwise
     */
    public static boolean isValidClick(InventoryClickEvent event, String inventoryName) {
        if (!event.getView().getTitle().equals(inventoryName)) {
            return false;
        }

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == XMaterial.AIR.parseMaterial() || !itemStack.hasItemMeta()) {
            return false;
        }

        event.setCancelled(true);
        return true;
    }
}