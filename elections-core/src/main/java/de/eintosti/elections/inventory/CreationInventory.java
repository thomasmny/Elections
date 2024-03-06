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
import de.eintosti.elections.inventory.listener.CreateListener;
import de.eintosti.elections.messages.MessagesOld;
import de.eintosti.elections.util.InventoryUtils;
import de.eintosti.elections.util.external.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;

public class CreateInventory {

    private final Settings settings;

    public CreateInventory(ElectionsPlugin plugin) {
        this.settings = plugin.getElection().getSettings();

        Bukkit.getPluginManager().registerEvents(new CreateListener(plugin), plugin);
    }

    public Inventory getInventory(Page page) {
        Inventory inventory = Bukkit.createInventory(null, 54, MessagesOld.getString("create_title"));

        InventoryUtils.addItemStack(inventory, 4, XMaterial.BOOK, MessagesOld.getString("create_startElection"));
        page.addCurrentPageMarkers(inventory);

        switch (page) {
            case GENERAL:
                InventoryUtils.addItemStack(
                        inventory, 20, XMaterial.OAK_SIGN, MessagesOld.getString("create_page1_position"),
                        MessagesOld.getStringList("create_page1_position_lore", new SimpleEntry<>("%position%", settings.getPosition()))
                );
                addMaxPlayers(inventory);
                InventoryUtils.addEnchantedItemStack(inventory, 24, XMaterial.NAME_TAG, settings.getMaxStatusLength(), MessagesOld.getString("create_page1_statusLength"), false, MessagesOld.getStringList("create_page1_statusLength_lore"));
                break;

            case NOMINATION:
            case VOTING:
                InventoryUtils.addUrlSkull(inventory, 19, MessagesOld.getString("create_page2_3_scoreboard"), "27712ca655128701ea3e5f28ddd69e6a8e63adf28052c51b2fd5adb538e1", MessagesOld.getStringList("create_page2_3_scoreboard_lore"));
                InventoryUtils.addUrlSkull(inventory, 20, MessagesOld.getString("create_page2_3_actionbar"), "c95d37993e594082678472bf9d86823413c250d4332a2c7d8c52de4976b362", MessagesOld.getStringList("create_page2_3_actionbar_lore"));
                InventoryUtils.addUrlSkull(inventory, 21, MessagesOld.getString("create_page2_3_titles"), "97e56140686e476aef5520acbabc239535ff97e24b14d87f4982f13675c", MessagesOld.getStringList("create_page2_3_titles_lore"));
                InventoryUtils.addUrlSkull(inventory, 22, MessagesOld.getString("create_page2_3_notifications"), "2ab5de74bb367e4a55a84a8843e05e94664af551a4b99cdf410436f0e444", MessagesOld.getStringList("create_page2_3_notifications_lore"));

                PhaseType phase = page.getPhase();
                int pageNum = phase == PhaseType.NOMINATION ? 2 : 3;
                String messageKey = "create_page" + pageNum + "_" + phase.name().toLowerCase() + "Length";

                addSettingsDye(inventory, 28, settings.isScoreboard(phase));
                addSettingsDye(inventory, 29, settings.isActionbar(phase));
                addSettingsDye(inventory, 30, settings.isTitle(phase));
                addSettingsDye(inventory, 31, settings.isNotification(phase));
                InventoryUtils.addItemStack(
                        inventory, 25, XMaterial.CLOCK, MessagesOld.getString(messageKey),
                        MessagesOld.getStringList(messageKey + "_lore", new SimpleEntry<>("%length%", StringUtils.formatTime(settings.getCountdown(phase))))
                );
                break;

            case FINISH:
                boolean enchant = settings.isFinishCommand();
                InventoryUtils.addEnchantedItemStack(inventory, 22, XMaterial.WRITABLE_BOOK, 1, MessagesOld.getString("create_page4_runCommands"), enchant, runCommandLore());
                break;
        }

        return inventory;
    }

    private void addSettingsDye(Inventory inventory, int position, boolean enabled) {
        XMaterial material = enabled ? XMaterial.LIME_DYE : XMaterial.GRAY_DYE;
        String displayNameKey = enabled ? "create_page2_3_setting_enabled" : "create_page2_3_setting_disabled";
        InventoryUtils.addItemStack(inventory, position, material, MessagesOld.getString(displayNameKey));
    }

    private void addMaxPlayers(Inventory inventory) {
        if (settings.isMaxEnabled()) {
            int amount = settings.getMaxCandidates() > 0 ? settings.getMaxCandidates() : 16;
            InventoryUtils.addEnchantedItemStack(inventory, 22, XMaterial.PLAYER_HEAD, amount, MessagesOld.getString("create_page1_maxCandidates_enabled"), true, MessagesOld.getStringList("create_page1_maxCandidates_enabled_lore"));
        } else {
            InventoryUtils.addEnchantedItemStack(inventory, 22, XMaterial.SKELETON_SKULL, 1, MessagesOld.getString("create_page1_maxCandidates_disabled"), false, MessagesOld.getStringList("create_page1_maxCandidates_disabled_lore"));
        }
    }

    private List<String> runCommandLore() {
        List<String> lore = MessagesOld.getStringList("create_page4_runCommands_lore");
        if (settings.isFinishCommand()) {
            lore.addAll(MessagesOld.getStringList("create_page4_runCommands_enabled_lore", new SimpleEntry<>("%commands%", getCommandList())));
        }
        return lore;
    }

    private String getCommandList() {
        List<String> commands = settings.getFinishCommands();
        if (commands.isEmpty()) {
            return "§7-";
        }

        StringBuilder stringBuilder = new StringBuilder();
        commands.forEach(command -> stringBuilder
                .append(MessagesOld.getString("create_page4_runCommands_enabled_command", new SimpleEntry<>("%command%", command)))
                .append("\n")
        );
        return stringBuilder.toString();
    }

    public enum Page {
        GENERAL(46, null),
        NOMINATION(48, PhaseType.NOMINATION),
        VOTING(50, PhaseType.VOTING),
        FINISH(52, null);

        private final int slot;
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
        public static Page matchPage(Inventory inventory) {
            return Arrays.stream(values())
                    .filter(page -> inventory.getItem(page.getSlot()).containsEnchantment(Enchantment.DURABILITY))
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
        public PhaseType getPhase() {
            return phase;
        }

        public void addCurrentPageMarkers(Inventory inventory) {
            addBorder(inventory);

            InventoryUtils.addItemStack(inventory, 46, XMaterial.ANVIL, MessagesOld.getString("create_settings"));
            InventoryUtils.addItemStack(inventory, 48, XMaterial.ARMOR_STAND, MessagesOld.getString("create_nominationPhase"));
            InventoryUtils.addItemStack(inventory, 50, XMaterial.FEATHER, MessagesOld.getString("create_votingPhase"));
            InventoryUtils.addItemStack(inventory, 52, XMaterial.OAK_SIGN, MessagesOld.getString("create_finishPhase"));

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