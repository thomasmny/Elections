package com.eintosti.elections.command.subcommand;

import com.cryptomorin.xseries.XSound;
import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.command.SubCommand;
import com.eintosti.elections.inventory.CreateInventory.Page;
import com.eintosti.elections.util.Messages;
import org.bukkit.entity.Player;

public class CreateSubCommand implements SubCommand {

    private final ElectionsPlugin plugin;

    public CreateSubCommand(ElectionsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("elections.start")) {
            Messages.sendMessage(player, "start_noPerms");
            return;
        }

        if (plugin.getElection().isActive()) {
            Messages.sendMessage(player, "start_alreadyStarted");
            return;
        }

        XSound.BLOCK_CHEST_OPEN.play(player);
        player.openInventory(plugin.getCreateInventory().getInventory(Page.GENERAL));
    }
}
