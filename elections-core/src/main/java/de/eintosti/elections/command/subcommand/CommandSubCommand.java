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
import de.eintosti.elections.api.election.Election;
import de.eintosti.elections.command.SubCommand;
import de.eintosti.elections.messages.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandSubCommand implements SubCommand {

    private final ElectionsPlugin plugin;

    public CommandSubCommand(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("elections.command")) {
            Messages.sendMessage(player, "election.finish_command.no_permission");
            return;
        }

        if (args.length == 0) {
            Messages.sendMessage(player, "election.finish_command.usage");
            return;
        }

        Election election = plugin.getElection();
        switch (election.getPhase().getPhaseType()) {
            case SETUP:
            case FINISHED:
                // Continue below
                break;
            default:
                Messages.sendMessage(player, "election.finish_command.already_started");
                return;
        }

        String command = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        election.getSettings().finishCommands().get().add(command);

        XSound.ENTITY_PLAYER_LEVELUP.play(player);
        Messages.sendMessage(player, "election.finish_command.added",
                Placeholder.unparsed("command", command)
        );
    }
}
