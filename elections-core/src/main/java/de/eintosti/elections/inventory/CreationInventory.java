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
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.api.election.settings.Settings;
import de.eintosti.elections.api.election.settings.Settings.Setting;
import de.eintosti.elections.inventory.listener.CreationListener;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.InventoryUtils;
import de.eintosti.elections.util.external.StringUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@NullMarked
public class CreationInventory {

    private final Settings settings;

    public CreationInventory(ElectionsPlugin plugin) {
        this.settings = plugin.getElection().getSettings();

        Bukkit.getPluginManager().registerEvents(new CreationListener(plugin), plugin);
    }

    public Inventory getInventory(Page page) {
        Inventory inventory = Bukkit.createInventory(null, 54, Messages.getString("creation.title"));

        InventoryUtils.addItemStack(inventory, 4, XMaterial.BOOK, Messages.getString("creation.election.start"));
        page.addCurrentPageMarkers(inventory);

        switch (page) {
            case GENERAL:
                InventoryUtils.addItemStack(
                        inventory, 20, XMaterial.OAK_SIGN,
                        Messages.getString("creation.position.title"),
                        Messages.getStringList("creation.position.lore",
                                Placeholder.unparsed("position", settings.position().get())
                        )
                );
                addMaxPlayers(inventory);
                InventoryUtils.addEnchantedItemStack(inventory, 24, XMaterial.NAME_TAG, settings.maxStatusLength().get(),
                        Messages.getString("creation.status_length.title"), false, Messages.getStringList("creation.status_length.lore")
                );
                break;

            case NOMINATION:
            case VOTING:
                PhaseType phaseKey = page.getPhase();

                InventoryUtils.addSkull(inventory, 19,
                        Messages.getString("creation.scoreboard.title"),
                        "27712ca655128701ea3e5f28ddd69e6a8e63adf28052c51b2fd5adb538e1",
                        Messages.getStringList("creation.scoreboard.lore")
                );
                addSettingsToggle(inventory, 28, settings.scoreboard(phaseKey));

                InventoryUtils.addSkull(inventory, 20,
                        Messages.getString("creation.actionbar.title"),
                        "c95d37993e594082678472bf9d86823413c250d4332a2c7d8c52de4976b362",
                        Messages.getStringList("creation.actionbar.lore")
                );
                addSettingsToggle(inventory, 29, settings.actionBar(phaseKey));

                InventoryUtils.addSkull(inventory, 21,
                        Messages.getString("creation.titles.title"),
                        "97e56140686e476aef5520acbabc239535ff97e24b14d87f4982f13675c",
                        Messages.getStringList("creation.titles.lore")
                );
                addSettingsToggle(inventory, 30, settings.title(phaseKey));

                InventoryUtils.addSkull(inventory, 22,
                        Messages.getString("creation.notifications.title"),
                        "2ab5de74bb367e4a55a84a8843e05e94664af551a4b99cdf410436f0e444",
                        Messages.getStringList("creation.notifications.lore")
                );
                addSettingsToggle(inventory, 31, settings.notification(phaseKey));

                InventoryUtils.addItemStack(
                        inventory, 24, XMaterial.CLOCK,
                        Messages.getString("creation.duration.title." + phaseKey.name().toLowerCase()),
                        Messages.getStringList("creation.duration.lore." + phaseKey.name().toLowerCase(),
                                Placeholder.unparsed("length", StringUtils.formatTime(settings.countdown(phaseKey).get()))
                        )
                );
                break;

            case FINISH:
                boolean enchant = !settings.finishCommands().get().isEmpty();
                InventoryUtils.addEnchantedItemStack(inventory, 22, XMaterial.WRITABLE_BOOK, 1,
                        Messages.getString("creation.commands.title"), enchant, getCommandLore()
                );
                break;
        }

        return inventory;
    }

    private void addSettingsToggle(Inventory inventory, int position, Setting<Boolean> setting) {
        boolean enabled = setting.get();
        XMaterial material = enabled ? XMaterial.LIME_DYE : XMaterial.GRAY_DYE;
        String displayNameKey = enabled ? "creation.setting.enabled" : "creation.setting.disabled";
        InventoryUtils.addItemStack(inventory, position, material, Messages.getString(displayNameKey));
    }

    private void addMaxPlayers(Inventory inventory) {
        boolean enabled = false;
        XMaterial material = XMaterial.SKELETON_SKULL;
        int amount = 1;

        if (settings.candidateLimitEnabled().get()) {
            enabled = true;
            material = XMaterial.PLAYER_HEAD;
            int maxCandidates = settings.maxCandidates().get();
            amount = maxCandidates > 0 ? maxCandidates : 16;
        }

        String key = enabled ? "enabled" : "disabled";
        InventoryUtils.addEnchantedItemStack(inventory, 22, material, amount,
                Messages.getString("creation.max_players.title." + key),
                enabled,
                Messages.getStringList("creation.max_players.lore." + key)
        );
    }

    private List<String> getCommandLore() {
        List<String> lore = Messages.getStringList("creation.commands.lore");

        List<String> commands = settings.finishCommands().get();
        if (commands.isEmpty()) {
            lore.add(Messages.getString("creation.commands.empty"));
        } else {
            for (String command : commands) {
                lore.add(Messages.getString("creation.commands.command", Placeholder.unparsed("command", command)));
            }
        }

        return lore;
    }

    public enum Page {
        GENERAL(46, null),
        NOMINATION(48, PhaseType.NOMINATION),
        VOTING(50, PhaseType.VOTING),
        FINISH(52, null);

        private final int slot;
        @Nullable
        private final PhaseType phase;

        Page(int slot, @Nullable PhaseType phase) {
            this.slot = slot;
            this.phase = phase;
        }

        /**
         * Matches the page based on the item in the specified inventory slot that contains the
         * {@link Enchantment#DURABILITY} enchantment.
         *
         * @param inventory The inventory to match the page from
         * @return The matched page, or {@link #GENERAL} if no match is found
         */
        public static Page fromInventory(Inventory inventory) {
            return Arrays.stream(values())
                    .filter(page -> inventory.getItem(page.getSlot()).containsEnchantment(Enchantment.DURABILITY))
                    .findFirst()
                    .orElse(GENERAL);
        }

        /**
         * Matches the page based on the given slot.
         * <p>
         * The slot is that which has to be clicked in order to open the respective page.
         *
         * @param slot The slot which was clicked
         * @return The matched page, or {@link #GENERAL} if no match is found
         */
        public static Page fromSlot(int slot) {
            return Arrays.stream(values())
                    .filter(page -> page.getSlot() == slot)
                    .findFirst()
                    .orElse(GENERAL);
        }

        /**
         * Retrieves the inventory slot associated with the current page.
         *
         * @return The inventory slot
         */
        public int getSlot() {
            return slot;
        }

        /**
         * Retrieves the phase associated with the current page.
         *
         * @return The phase
         */
        @Nullable
        public PhaseType getPhase() {
            return phase;
        }

        public void addCurrentPageMarkers(Inventory inventory) {
            addBorder(inventory);

            InventoryUtils.addItemStack(inventory, 46, XMaterial.ANVIL, Messages.getString("creation.page.general"));
            InventoryUtils.addItemStack(inventory, 48, XMaterial.ARMOR_STAND, Messages.getString("creation.page.nomination"));
            InventoryUtils.addItemStack(inventory, 50, XMaterial.FEATHER, Messages.getString("creation.page.voting"));
            InventoryUtils.addItemStack(inventory, 52, XMaterial.OAK_SIGN, Messages.getString("creation.page.finish"));

            inventory.getItem(slot).addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }

        private void addBorder(Inventory inv) {
            for (int i : new int[]{0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44}) {
                InventoryUtils.addGlassPane(inv, i);
            }
            InventoryUtils.addItemStack(inv, slot - 9, XMaterial.LIME_STAINED_GLASS_PANE, "§7");
        }
    }
}