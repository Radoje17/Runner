package me.radoje17.runner;

import me.radoje17.runner.utils.ConfigUtils;
import me.radoje17.runner.utils.PlayerUtils;
import me.radoje17.runner.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Runner extends JavaPlugin {

    private static Runner runner;

    private static RunnerGameManager runnerGameManager;

    public static Runner getInstance() {
        return runner;
    }

    @Override
    public void onEnable() {
        this.runner = this;

        this.runnerGameManager = new RunnerGameManager();

        loadListeners();
        loadCommands();

        new PlayerUtils();
        new UUIDUtils();
        new RunnerGameManager();

        new ConfigUtils();

        new File(getDataFolder() + "/schematics").mkdirs();

    }

    @Override
    public void onDisable() {
        runnerGameManager.forceStopAllGames();
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new UUIDUtils(), this);
        Bukkit.getPluginManager().registerEvents(runnerGameManager, this);
    }

    private void loadCommands() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("join")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Ova komanda je samo za igrace.");
                return false;
            }

            runnerGameManager.getGame("map1").addPlayer((Player) sender);
        }

        return false;
    }

    public RunnerGameManager getRunnerGameManager() {
        return runnerGameManager;
    }
}
