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
import de.eintosti.elections.api.election.Election;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.api.election.settings.Settings;
import de.eintosti.elections.inventory.CreateInventory;
import de.eintosti.elections.inventory.CreateInventory.Page;
import de.eintosti.elections.messages.MessagesOld;
import de.eintosti.elections.util.InventoryUtils;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.AnvilGUI.ResponseAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;

public class CreateListener implements Listener {

    private final ElectionsPlugin plugin;
    private final Election election;
    private final Settings settings;

    public CreateListener(ElectionsPlugin plugin) {
        this.plugin = plugin;
        this.election = plugin.getElection();
        this.settings = election.getSettings();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!InventoryUtils.isValidClick(event, MessagesOld.getString("create_title"))) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("elections.create")) {
            return;
        }

        CreateInventory createInventory = plugin.getCreateInventory();
        Page page = Page.matchPage(event.getInventory());

        switch (event.getSlot()) {
            case 4:
                player.closeInventory();
                election.start();
                return;
            case 46:
            case 48:
            case 50:
            case 52:
                player.openInventory(createInventory.getInventory(page));
                XSound.ENTITY_ITEM_PICKUP.play(player);
                return;
        }

        switch (page) {
            case GENERAL: {
                switch (event.getSlot()) {
                    case 20:
                        XSound.ENTITY_ITEM_PICKUP.play(player);
                        openPositionAnvil(player);
                        return;

                    case 22:
                        if (event.getClick().isShiftClick()) {
                            settings.setMaxEnabled(!settings.isMaxEnabled());
                            break;
                        }

                        if (!settings.isMaxEnabled()) {
                            XSound.ENTITY_ITEM_BREAK.play(player);
                            return;
                        }

                        int maxCandidates = settings.getMaxCandidates();
                        switch (event.getClick()) {
                            case LEFT:
                                if ((maxCandidates - 1) > 0) {
                                    settings.setMaxCandidates(settings.getMaxCandidates() - 1);
                                }
                                break;
                            case RIGHT:
                                if ((maxCandidates + 1) < 64) {
                                    settings.setMaxCandidates(settings.getMaxCandidates() + 1);
                                }
                                break;
                        }
                        break;

                    case 24:
                        int statusLength = settings.getMaxStatusLength();
                        switch (event.getClick()) {
                            case LEFT:
                                if ((statusLength - 1) > 0) {
                                    settings.setMaxStatusLength(settings.getMaxStatusLength() - 1);
                                }
                                break;
                            case RIGHT:
                                if ((statusLength + 1) < 64) {
                                    settings.setMaxStatusLength(settings.getMaxStatusLength() + 1);
                                }
                                break;
                        }
                        break;

                    default:
                        return;
                }

                XSound.ENTITY_FISHING_BOBBER_RETRIEVE.play(player);
                player.openInventory(createInventory.getInventory(page));
                return;
            }

            case NOMINATION:
            case VOTING: {
                PhaseType settingPhase = page == Page.NOMINATION ? PhaseType.NOMINATION : PhaseType.VOTING;

                switch (event.getSlot()) {
                    case 25:
                        player.openInventory(plugin.getTimeInventory().getInventory(settingPhase));
                        XSound.ENTITY_CHICKEN_EGG.play(player);
                        return;
                    case 28:
                        settings.setScoreboard(settingPhase, !settings.isScoreboard(settingPhase));
                        break;
                    case 29:
                        settings.setActionbar(settingPhase, !settings.isActionbar(settingPhase));
                        break;
                    case 30:
                        settings.setTitle(settingPhase, !settings.isTitle(settingPhase));
                        break;
                    case 31:
                        settings.setNotification(settingPhase, !settings.isNotification(settingPhase));
                        break;
                    default:
                        return;
                }

                player.openInventory(createInventory.getInventory(page));
                XSound.ENTITY_FISHING_BOBBER_RETRIEVE.play(player);
                return;
            }

            case FINISH: {
                if (event.getSlot() == 22) {
                    if (event.getClick().isShiftClick()) {
                        settings.setFinishCommand(!settings.isFinishCommand());
                        player.openInventory(createInventory.getInventory(Page.FINISH));
                        XSound.ENTITY_FISHING_BOBBER_RETRIEVE.play(player);
                        return;
                    }

                    if (!settings.isFinishCommand()) {
                        XSound.ENTITY_ITEM_BREAK.play(player);
                        return;
                    }

                    if (event.isLeftClick()) {
                        XSound.ENTITY_ITEM_PICKUP.play(player);
                        openCommandAnvil(player);
                    } else if (event.isRightClick()) {
                        List<String> commands = settings.getFinishCommands();
                        String lastCommand = commands.get(commands.size() - 1);
                        if (lastCommand == null) {
                            return;
                        }

                        settings.getFinishCommands().remove(lastCommand);
                        player.openInventory(createInventory.getInventory(Page.FINISH));
                        MessagesOld.sendMessage(player, "command_removed", new SimpleEntry<>("%command%", lastCommand));
                    }
                }
                break;
            }
        }
    }

    private void openPositionAnvil(Player player) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    Player anvilPlayer = stateSnapshot.getPlayer();
                    String position = stateSnapshot.getText().trim();
                    settings.setPosition(position);

                    XSound.ENTITY_PLAYER_LEVELUP.play(anvilPlayer);
                    MessagesOld.sendMessage(anvilPlayer, "position_set", new SimpleEntry<>("%position%", position));
                    return Collections.singletonList(ResponseAction.close());
                })
                .text(settings.getPosition())
                .plugin(plugin)
                .open(player);
    }

    private void openCommandAnvil(Player player) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    Player anvilPlayer = stateSnapshot.getPlayer();
                    String command = stateSnapshot.getText().trim();
                    if (command.startsWith("/")) {
                        command = command.substring(1);
                    }
                    settings.getFinishCommands().add(command);

                    XSound.ENTITY_PLAYER_LEVELUP.play(anvilPlayer);
                    MessagesOld.sendMessage(anvilPlayer, "command_added", new SimpleEntry<>("%command%", command));
                    return Collections.singletonList(ResponseAction.close());
                })
                .text("Variable: %player%")
                .plugin(plugin)
                .open(player);
    }
}