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
package de.eintosti.elections.election.phase;

import com.cryptomorin.xseries.XSound;
import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.phase.PhaseType;
import de.eintosti.elections.election.ElectionImpl;
import de.eintosti.elections.messages.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NominationPhase extends ScoreboardPhase {

    private final ElectionsPlugin plugin;

    public NominationPhase(ElectionsPlugin plugin, ElectionImpl election) {
        super(election, PhaseType.NOMINATION);

        this.plugin = plugin;
    }

    @Override
    public AbstractPhase getNextPhase() {
        return new VotingPhase(plugin, election);
    }

    @Override
    public void onStart() {
        super.onStart();

        Bukkit.getOnlinePlayers().forEach(pl -> {
            XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.play(pl);
            Messages.sendMessage(pl, "election.nomination.started",
                    Placeholder.unparsed("position", settings.position().get())
            );
            if (settings.title(super.getPhaseType()).get()) {
                Messages.sendTitle(pl, "election.nomination.title", "election.nomination.subtitle");
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!settings.notification(super.getPhaseType()).get()) {
            return;
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Messages.sendMessage(event.getPlayer(), "election.nomination.started",
                    Placeholder.unparsed("position", settings.position().get())
            );
        }, 1L);
    }
}