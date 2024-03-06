package com.eintosti.elections.election.phase;

import com.cryptomorin.xseries.XSound;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.settings.SettingsPhase;
import com.eintosti.elections.election.ElectionImpl;
import com.eintosti.elections.election.ElectionSettings;
import com.eintosti.elections.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

public class VotingPhase extends ScoreboardPhase {

    private final ElectionsPlugin plugin;
    private final ElectionImpl election;
    private final ElectionSettings settings;

    public VotingPhase(ElectionsPlugin plugin) {
        super(plugin.getElection(), SettingsPhase.VOTING, getPlaceholders());

        this.plugin = plugin;
        this.election = plugin.getElection();
        this.settings = election.getSettings();
    }

    private static List<Entry<String, String>> getPlaceholders() {
        return Arrays.asList(
                new SimpleEntry<>("%phase%", Messages.getString("scoreboard_phase_voting")),
                new SimpleEntry<>("%time%", "XX:XX:XX")
        );
    }

    @Override
    public AbstractPhase getNextPhase() {
        return new FinishPhase(plugin);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (election.getNominations().isEmpty()) {
            election.prematureStop();
            Bukkit.getOnlinePlayers().forEach(pl -> {
                Messages.sendMessage(pl, "voting_noPlayers");
                XSound.ENTITY_ENDER_DRAGON_DEATH.play(pl);
            });
            return;
        }

        Bukkit.getOnlinePlayers().forEach(pl -> {
            if (pl.getOpenInventory().getTitle().equals(Messages.getString("nominate_title"))) {
                pl.closeInventory();
            }

            if (settings.isTitle(super.getSettingsPhase())) {
                pl.sendTitle(Messages.getString("voting_title"), Messages.getString("voting_subtitle"));
            }
            Messages.sendMessage(pl, "voting_start", new SimpleEntry<>("%position%", settings.getPosition()));
            XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.play(pl);
        });
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!settings.isNotification(super.getSettingsPhase())) {
            return;
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Messages.sendMessage(event.getPlayer(), "voting_start", new SimpleEntry<>("%position%", settings.getPosition()));
        }, 1L);
    }
}