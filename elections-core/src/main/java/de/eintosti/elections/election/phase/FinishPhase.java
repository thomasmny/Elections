package com.eintosti.elections.election.phase;

import com.cryptomorin.xseries.XSound;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.candidate.Candidate;
import com.eintosti.elections.api.election.settings.SettingsPhase;
import com.eintosti.elections.election.ElectionImpl;
import com.eintosti.elections.election.ElectionSettings;
import com.eintosti.elections.util.Messages;
import com.eintosti.elections.util.external.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FinishPhase extends AbstractPhase {

    private final ElectionsPlugin plugin;
    private final ElectionImpl election;

    private int winningCount = 0;
    private final List<Candidate> winners;
    private final List<UUID> notified;

    public FinishPhase(ElectionsPlugin plugin) {
        this.plugin = plugin;
        this.election = plugin.getElection();

        this.winners = new ArrayList<>();
        this.notified = new ArrayList<>();
    }

    @Override
    public AbstractPhase getNextPhase() {
        plugin.resetElection();
        return plugin.getElection().getPhase();
    }

    @Override
    public SettingsPhase getSettingsPhase() {
        return SettingsPhase.FINISHED;
    }

    @Override
    public void onStart() {
        findWinners();

        List<String> message = getWinningMessage();
        if (!winners.isEmpty()) {
            ElectionSettings settings = election.getSettings();
            if (settings.isFinishCommand()) {
                winners.forEach(winner -> settings.getFinishCommands().forEach(command -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", winner.getName()));
                }));
            }
        }

        Bukkit.getOnlinePlayers().forEach(pl -> {
            if (pl.getOpenInventory().getTitle().equals(Messages.getString("vote_title"))) {
                pl.closeInventory();
            }

            pl.sendMessage(message.toArray(new String[0]));
            XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.play(pl);
            notified.add(pl.getUniqueId());
        });
    }

    public void findWinners() {
        for (Map.Entry<UUID, Integer> entry : election.getCandidateVoteCount().entrySet()) {
            int numberOfVotes = entry.getValue();
            if (numberOfVotes < winningCount || numberOfVotes == 0) {
                continue;
            }

            if (numberOfVotes > winningCount) {
                winners.clear();
                winningCount = numberOfVotes;
            }

            winners.add(election.getCandidate(entry.getKey()));
        }
    }

    public List<String> getWinningMessage() {
        List<String> message = new ArrayList<>();

        if (!winners.isEmpty()) {
            if (winners.size() == 1) {
                Candidate winner = winners.get(0);
                message.addAll(Messages.getStringList("online_singleWinner", new SimpleEntry<>("%winner%", winner.getName()), new SimpleEntry<>("%votes%", winningCount)));
            } else {
                String winnerNames = StringUtils.join(winners.stream().map(Candidate::getName).collect(Collectors.toList()), ", ");
                message.addAll(Messages.getStringList("online_multipleWinners", new SimpleEntry<>("%winner%", winnerNames), new SimpleEntry<>("%votes%", winningCount)));
            }
        } else {
            message.addAll(Messages.getStringList("online_noWinner"));
        }

        return message;
    }

    @Override
    public void onFinish() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (notified.contains(player.getUniqueId())) {
            return;
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            getWinningMessage().forEach(player::sendMessage);
        }, 1L);
        notified.add(player.getUniqueId());
    }
}