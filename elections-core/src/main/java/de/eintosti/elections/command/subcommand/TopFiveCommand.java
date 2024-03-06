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
package de.eintosti.elections.command.subcommand;

import com.cryptomorin.xseries.XSound;
import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.command.SubCommand;
import de.eintosti.elections.election.ElectionImpl;
import de.eintosti.elections.messages.Messages;
import org.bukkit.entity.Player;

public class TopFiveCommand implements SubCommand {

    private final ElectionsPlugin plugin;

    public TopFiveCommand(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        final ElectionImpl election = plugin.getElection();

        switch (election.getPhase().getPhaseType()) {
            case VOTING:
            case FINISHED:
                // Continue below
                break;
            default:
                Messages.sendMessage(player, "election.vote.not_started");
                return;
        }

        XSound.BLOCK_CHEST_OPEN.play(player);
        player.openInventory(plugin.getTopInventory().getInventory());
    }
}
