package me.radoje17.runner.exceptions;

import org.bukkit.command.CommandSender;

public class NotInGameException extends RuntimeException {

    public NotInGameException(CommandSender sender) {
        sender.sendMessage("Nisi u igri.");
    }

}
