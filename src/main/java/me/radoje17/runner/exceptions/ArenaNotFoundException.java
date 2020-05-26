package me.radoje17.runner.exceptions;

import org.bukkit.command.CommandSender;

public class ArenaNotFoundException extends RuntimeException {

    public ArenaNotFoundException(CommandSender sender, String arenaName) {
        sender.sendMessage("Arena " + arenaName + " nije pronadjena.");
    }

}
