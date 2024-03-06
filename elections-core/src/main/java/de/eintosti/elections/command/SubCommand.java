package com.eintosti.elections.command;

import org.bukkit.entity.Player;

public interface SubCommand {

    void execute(Player player, String[] args);
}