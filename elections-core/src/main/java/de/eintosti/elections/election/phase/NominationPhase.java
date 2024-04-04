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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    //TODO: Remove fake candidates
    @Override
    public void onFinish() {
        Map<String, String> candidates = new HashMap<String, String>() {{
            put("GommeHD", "e9013c2f-da01-425f-a48b-516f55e94386");
            put("Deennis", "6cf19618-9662-415a-b8c8-aab1f3e0f9f7");
            put("Kreuzrausch", "8f12bdd1-fb15-4386-9bf3-cbdbb4495cbd");
            put("BergFelix", "c76d607d-4ec0-4bbd-8199-de0a70bb93b9");
            put("Petaa97", "89ea5c0f-3311-4afc-890b-1340fa6a4e8b");
            put("Klaus", "3fef889a-fb68-4dfb-bcee-38d56637f6f6");
            put("xAuster", "bd42ecf6-daf0-477b-954a-2f303ed6c463");
            put("DerAutist", "1b31a68e-1c89-4378-88ca-87b8522309da");
            put("hypixel", "f7c77d99-9f15-4a66-a87d-c4a51ef30d19");
        }};
        candidates.forEach((name, uuid) -> plugin.getElection().nominate(UUID.fromString(uuid), name));

        super.onFinish();
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