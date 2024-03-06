package com.eintosti.elections.util;

import com.cryptomorin.xseries.XMaterial;
import com.eintosti.elections.util.external.ItemSkulls;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class InventoryUtils {

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

    @SuppressWarnings("deprecation")
    public static ItemStack getSkull(String displayName, String skullOwner, List<String> lore) {
        ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        skullMeta.setOwner(skullOwner);
        skullMeta.setDisplayName(displayName);
        skullMeta.setLore(lore);
        skull.setItemMeta(skullMeta);

        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static void addSkull(Inventory inventory, int position, String displayName, String skullOwner, List<String> lore) {
        inventory.setItem(position, getSkull(displayName, skullOwner, lore));
    }

    public static ItemStack getUrlSkull(String displayName, String url, List<String> lore) {
        ItemStack skull = ItemSkulls.getSkull(url);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        skullMeta.setDisplayName(displayName);
        skullMeta.setLore(lore);
        skullMeta.addItemFlags(ItemFlag.values());
        skull.setItemMeta(skullMeta);

        return skull;
    }

    public static void addUrlSkull(Inventory inventory, int position, String displayName, String url, List<String> lore) {
        inventory.setItem(position, getUrlSkull(displayName, url, lore));
    }

    public static void addUrlSkull(Inventory inventory, int position, String displayName, String url, String... lore) {
        addUrlSkull(inventory, position, displayName, url, Arrays.asList(lore));
    }

    /**
     * Checks whether the clicked {@link Inventory} is equivalent to the inventory with the given name.<br>
     * Also makes sure that the clicked {@link ItemStack} is not {@code null} and if so, cancels the event.
     *
     * @param event         The inventory click event
     * @param inventoryName The name of the inventory to compare with
     * @return {@code true} if the above is true, {@code false} otherwise
     */
    public static boolean checkIfValidClick(InventoryClickEvent event, String inventoryName) {
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