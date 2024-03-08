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
import de.eintosti.elections.api.election.settings.Settings.Setting;
import de.eintosti.elections.inventory.CreationInventory;
import de.eintosti.elections.inventory.CreationInventory.Page;
import de.eintosti.elections.messages.Messages;
import de.eintosti.elections.util.InventoryUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.AnvilGUI.ResponseAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.List;

@NullMarked
public class CreationListener implements Listener {

    private final ElectionsPlugin plugin;
    private final Election election;
    private final Settings settings;

    public CreationListener(ElectionsPlugin plugin) {
        this.plugin = plugin;
        this.election = plugin.getElection();
        this.settings = election.getSettings();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!InventoryUtils.isValidClick(event, Messages.getString("creation.title"))) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("elections.create")) {
            return;
        }

        CreationInventory createInventory = plugin.getCreationInventory();
        Page page = Page.fromInventory(event.getInventory());

        switch (event.getSlot()) {
            case 4:
                player.closeInventory();
                election.start();
                return;
            case 46:
            case 48:
            case 50:
            case 52:
                player.openInventory(createInventory.getInventory(Page.fromSlot(event.getSlot())));
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
                        Setting<Boolean> maxCandidatesEnabled = settings.candidateLimitEnabled();
                        if (event.getClick().isShiftClick()) {
                            maxCandidatesEnabled.set(!maxCandidatesEnabled.get());
                            break;
                        }

                        if (!maxCandidatesEnabled.get()) {
                            XSound.ENTITY_ITEM_BREAK.play(player);
                            return;
                        }

                        modifyIntegerSetting(event, settings.maxCandidates());
                        break;

                    case 24:
                        modifyIntegerSetting(event, settings.maxStatusLength());
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
                    case 24:
                        player.openInventory(plugin.getTimeInventory().getInventory(settingPhase));
                        XSound.ENTITY_CHICKEN_EGG.play(player);
                        return;
                    case 28:
                        Setting<Boolean> scoreboard = settings.scoreboard(settingPhase);
                        scoreboard.set(!scoreboard.get());
                        break;
                    case 29:
                        Setting<Boolean> actionBar = settings.actionBar(settingPhase);
                        actionBar.set(!actionBar.get());
                        break;
                    case 30:
                        Setting<Boolean> title = settings.title(settingPhase);
                        title.set(!title.get());
                        break;
                    case 31:
                        Setting<Boolean> notification = settings.notification(settingPhase);
                        notification.set(!notification.get());
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
                    if (event.isLeftClick()) {
                        XSound.ENTITY_ITEM_PICKUP.play(player);
                        openCommandAnvil(player);
                    } else if (event.isRightClick()) {
                        List<String> finishCommands = settings.finishCommands().get();
                        if (finishCommands.isEmpty()) {
                            return;
                        }

                        String lastCommand = finishCommands.get(finishCommands.size() - 1);
                        finishCommands.remove(lastCommand);
                        player.openInventory(createInventory.getInventory(Page.FINISH));
                        Messages.sendMessage(player, "election.finish_command.removed",
                                Placeholder.unparsed("command", lastCommand)
                        );
                    }
                }
                break;
            }
        }
    }

    private void modifyIntegerSetting(InventoryClickEvent event, Setting<Integer> setting) {
        switch (event.getClick()) {
            case LEFT:
                if ((setting.get() - 1) > 0) {
                    setting.set(setting.get() - 1);
                }
                break;
            case RIGHT:
                if ((setting.get() + 1) < 64) {
                    setting.set(setting.get() + 1);
                }
                break;
        }
    }

    private void openPositionAnvil(Player player) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    Player anvilPlayer = stateSnapshot.getPlayer();
                    String position = stateSnapshot.getText().trim();
                    settings.position().set(position);

                    XSound.ENTITY_PLAYER_LEVELUP.play(anvilPlayer);
                    Messages.sendMessage(anvilPlayer, "election.position.set",
                            Placeholder.unparsed("position", position)
                    );
                    return Collections.singletonList(ResponseAction.close());
                })
                .text(settings.position().get())
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
                    settings.finishCommands().get().add(command);

                    XSound.ENTITY_PLAYER_LEVELUP.play(anvilPlayer);
                    Messages.sendMessage(anvilPlayer, "election.finish_command.added",
                            Placeholder.unparsed("command", command)
                    );
                    return Collections.singletonList(ResponseAction.close());
                })
                .text("Variable: %player%")
                .plugin(plugin)
                .open(player);
    }
}