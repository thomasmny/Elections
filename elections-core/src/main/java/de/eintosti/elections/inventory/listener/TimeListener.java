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
package de.eintosti.elections.inventory.listener;

import com.cryptomorin.xseries.XSound;
import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.api.election.settings.Settings.Type;
import de.eintosti.elections.election.ElectionSettings;
import de.eintosti.elections.inventory.CreationInventory.Page;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TimeListener implements Listener {

    private static final int PHASE_INFORMATION_SLOT = 13;

    private static final int ONE_MINUTE = 60;
    private static final int ONE_HOUR = 60 * ONE_MINUTE;
    private static final int ONE_DAY = 24 * ONE_HOUR;

    private final ElectionsPlugin plugin;
    private final ElectionSettings settings;

    public TimeListener(ElectionsPlugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getElection().getSettings();
    }

    @EventHandler
    public void onInventoryClickTime(InventoryClickEvent event) {
        if (!InventoryUtils.isValidClick(event, Messages.getString("time.title"))) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String displayName = event.getInventory().getItem(PHASE_INFORMATION_SLOT).getItemMeta().getDisplayName();
        PhaseType phase = displayName.equals(Messages.getString("time.phase.title.nomination")) ? PhaseType.NOMINATION : PhaseType.VOTING;

        switch (event.getSlot()) {
            case 10:
                tryCountdownReduction(phase, player, ONE_DAY);
                break;
            case 11:
                tryCountdownReduction(phase, player, ONE_HOUR);
                break;
            case 12:
                tryCountdownReduction(phase, player, ONE_MINUTE);
                break;
            case 13:
                Page page = phase == PhaseType.NOMINATION ? Page.NOMINATION : Page.VOTING;
                player.openInventory(plugin.getCreationInventory().getInventory(page));
                break;
            case 14:
                modifyCountdown(phase, player, ONE_MINUTE);
                break;
            case 15:
                modifyCountdown(phase, player, ONE_HOUR);
                break;
            case 16:
                modifyCountdown(phase, player, ONE_DAY);
                break;
            default:
                return;
        }

        XSound.ENTITY_ITEM_PICKUP.play(player);
    }

    /**
     * Attempts to reduce the countdown by the given amount of seconds. Will fail if the countdown would be
     * negative after the subtraction.
     *
     * @param phase   The phase which to reduce the countdown of
     * @param player  The player attempting to reduce the countdown
     * @param seconds The amount of seconds to reduce the countdown by
     */
    private void tryCountdownReduction(PhaseType phase, Player player, int seconds) {
        if ((settings.countdown(phase).get() - seconds) > 0) {
            XSound.ENTITY_ITEM_PICKUP.play(player);
            modifyCountdown(phase, player, -seconds);
        } else {
            XSound.ENTITY_ITEM_BREAK.play(player);
            player.sendMessage(Messages.getString("time.invalid_duration"));
        }
    }

    /**
     * Adds the given amount of seconds to the countdown.
     *
     * @param phase   The phase which to increment the countdown of
     * @param player  The player attempting to increment the countdown
     * @param seconds The amount of seconds to increment the countdown by
     */
    private void modifyCountdown(PhaseType phase, Player player, int seconds) {
        Type<Integer> countdown = settings.countdown(phase);
        countdown.set(countdown.get() + seconds);
        XSound.ENTITY_ITEM_PICKUP.play(player);
        player.openInventory(plugin.getTimeInventory().getInventory(phase));
    }
}