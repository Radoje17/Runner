package me.radoje17.runner;

import me.radoje17.runner.exceptions.NotInGameException;
import me.radoje17.runner.exceptions.PlayerCommandException;
import me.radoje17.runner.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
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

    private static World runnerWorld;

    public static World getRunnerWorld() {
        return runnerWorld;
    }

    @Override
    public void onEnable() {
        this.runner = this;

        this.runnerGameManager = new RunnerGameManager();

        loadListeners();
        loadCommands();

        new PlayerUtils();
        new UUIDUtils();

        new ConfigUtils();

        new File(getDataFolder() + "/schematics").mkdirs();


        this.runnerWorld = WorldUtils.createWorld("runner");

        for (String s : ConfigUtils.getArenaList()) {
            Runner.getInstance().getRunnerGameManager().getGame(s);
        }

    }

    @Override
    public void onDisable() {
        if (Bukkit.getWorld("runner") != null) {
            for (Player p : runnerWorld.getPlayers()) {
                p.setGameMode(GameMode.SPECTATOR);
                p.teleport(ConfigUtils.getLobby());
                if (runnerGameManager.inventories.containsKey(p)) {
                    p.getInventory().setContents(runnerGameManager.inventories.get(p));
                }
                p.setGameMode(GameMode.ADVENTURE);
            }

            WorldUtils.deleteWorld(Bukkit.getWorld("runner"));
        }
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new InventoryUtils(), this);
        Bukkit.getPluginManager().registerEvents(new UUIDUtils(), this);
        Bukkit.getPluginManager().registerEvents(runnerGameManager, this);
    }

    private void loadCommands() {
        getCommand("runner").setExecutor(new RunnerCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("leave")) {

            if (!(sender instanceof Player)) {
                throw new PlayerCommandException(sender);
            }

            if (!runnerGameManager.isWaitingOrInGame((Player) sender)) {
                throw new NotInGameException(sender);
            }

            runnerGameManager.getGame((Player) sender).removePlayer((Player) sender);
            ((Player) sender).sendMessage("Napustili ste igru.");
        }

        if (command.getName().equalsIgnoreCase("join")) {
            if (!(sender instanceof Player)) {
                throw new PlayerCommandException(sender);
            }

            ((Player) sender).openInventory(InventoryUtils.getMapsInventory());
        }
        return false;
    }

    public RunnerGameManager getRunnerGameManager() {
        return runnerGameManager;
    }
}
