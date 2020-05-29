package me.radoje17.runner.exceptions;

import org.bukkit.command.CommandSender;

public class PlayerCommandException extends RuntimeException {

    public PlayerCommandException(CommandSender sender) {
        sender.sendMessage("Ova komanda je namenjana samo igracima.");
    }

}
