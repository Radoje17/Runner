package me.radoje17.runner;

import me.radoje17.runner.utils.InventoryUtils;
import me.radoje17.runner.utils.PlayerUtils;
import me.radoje17.runner.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RunnerCommand implements CommandExecutor, TabCompleter {

    private void posaljiPoruku(CommandSender sender, String poruka) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', poruka));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            posaljiPoruku(sender, "&8&m------------- &eRunner &8&m-------------");
            if (sender instanceof Player) {
                posaljiPoruku(sender, "&e/" + label + " browse &8- &7Izaberi mapu na kojoj zelis da igras");
            }
            posaljiPoruku(sender, "&e/" + label + " stats [igrac] &8- &7Proveri stats nekog igraca");
            return false;
        }
        if (args[0].equalsIgnoreCase("browse") && sender instanceof Player) {
            ((Player) sender).openInventory(InventoryUtils.getMapsInventory());
        }
        if (args[0].equalsIgnoreCase("stats")) {
            if (args.length == 0 && !(sender instanceof Player)) {
                posaljiPoruku(sender, "&7[&eRunner&7] &7Molim Vas da koristite sledecu sintaksu:\n    /" + label + " <igrac>");
                return false;
            }

            String igrac = UUIDUtils.getUUID(sender.getName());

            if (args.length > 1) {
                igrac = UUIDUtils.getUUID(args[1]);
                if (igrac == null) {
                    posaljiPoruku(sender, "&7[&eRunner&7] &7Igrac " + args[1] + " nije pronadjen.");
                    return false;
                }
            }

            igrac = Bukkit.getOfflinePlayer(UUID.fromString(igrac)).getName();
            posaljiPoruku(sender, "&7[&eRunner&7] &7Stats igraca &e" + igrac + "&7:");
            posaljiPoruku(sender, "&7[&eRunner&7] &7Igrano igara: &e" + (PlayerUtils.getWins(igrac) + PlayerUtils.getLosses(igrac)));
            posaljiPoruku(sender, "&7[&eRunner&7] &7Pobede: &e" + PlayerUtils.getWins(igrac));
            posaljiPoruku(sender, "&7[&eRunner&7] &7Gubici: &e" + PlayerUtils.getLosses(igrac));
            posaljiPoruku(sender, "&7[&eRunner&7] &7Odnos pobeda/gubici: &e" + new DecimalFormat("#.##").format((double)PlayerUtils.getWins(igrac)/PlayerUtils.getLosses(igrac)));
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
