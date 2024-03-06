package com.eintosti.elections.election.phase;

import com.cryptomorin.xseries.XSound;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.settings.Settings;
import com.eintosti.elections.api.election.settings.SettingsPhase;
import com.eintosti.elections.election.candidate.ElectionCandidate;
import com.eintosti.elections.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class NominationPhase extends ScoreboardPhase {

    private final ElectionsPlugin plugin;
    private final Settings settings;

    public NominationPhase(ElectionsPlugin plugin) {
        super(plugin.getElection(), SettingsPhase.NOMINATION, getPlaceholders());

        this.plugin = plugin;
        this.settings = plugin.getElection().getSettings();
    }

    private static List<Entry<String, String>> getPlaceholders() {
        return Arrays.asList(
                new SimpleEntry<>("%phase%", Messages.getString("scoreboard_phase_nomination")),
                new SimpleEntry<>("%time%", "XX:XX:XX")
        );
    }

    @Override
    public AbstractPhase getNextPhase() {
        return new VotingPhase(plugin);
    }

    @Override
    public void onStart() {
        super.onStart();

        Bukkit.getOnlinePlayers().forEach(pl -> {
            if (settings.isTitle(super.getSettingsPhase())) {
                pl.sendTitle(Messages.getString("nomination_title"), Messages.getString("nomination_subtitle"));
            }
            Messages.sendMessage(pl, "nomination_start", new SimpleEntry<>("%position%", settings.getPosition()));
            XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.play(pl);
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
        candidates.forEach((name, uuid) -> {
            plugin.getElection().addNomination(new ElectionCandidate(UUID.fromString(uuid), name));
        });

        super.onFinish();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!settings.isNotification(super.getSettingsPhase())) {
            return;
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Messages.sendMessage(event.getPlayer(), "nomination_start", new SimpleEntry<>("%position%", settings.getPosition()));
        }, 1L);
    }
}