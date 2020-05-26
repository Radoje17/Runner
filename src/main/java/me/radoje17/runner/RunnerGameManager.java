package me.radoje17.runner;

import me.radoje17.runner.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RunnerGameManager implements Listener {

    List<RunnerGame> activeRunnerGameList;
    List<RunnerGame> waitingRunnerGameList;

    HashMap<Player, RunnerGame> activePlayers;
    HashMap<Player, RunnerGame> waitingPlayers;

    public RunnerGameManager() {
        this.activeRunnerGameList = new ArrayList<>();
        this.waitingRunnerGameList = new ArrayList<>();
        this.activePlayers = new HashMap<>();
        this.waitingPlayers = new HashMap<>();
    }


    public void forceStopAllGames() {
        for (RunnerGame g : waitingRunnerGameList) {
            g.forceRemoveAllPlayers();
        }

        for (RunnerGame g : activeRunnerGameList) {
            g.forceRemoveAllPlayers();
        }
    }

    public RunnerGame getGame(String arenaName) {
        for (RunnerGame game : waitingRunnerGameList) {
            if (game.getArenaName().equals(arenaName) && game.getSlots() > game.getPlayerCount()) {
                return game;
            }
         }

        RunnerGame game = new RunnerGame(arenaName);
        waitingRunnerGameList.add(game);
        return game;
    }


    public boolean isInGame(Player p) {
        return activePlayers.containsKey(p);
}

    public boolean isWaiting(Player p) {
        return waitingPlayers.containsKey(p);
    }

    public boolean isWaitingOrInGame(Player p) {
        return isInGame(p) || isWaiting(p);
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (activePlayers.containsKey(e.getPlayer())) {
            RunnerGame game = activePlayers.get(e.getPlayer());

            Location l = e.getPlayer().getLocation();
            l.setY(l.getY()-1);

            if (e.getPlayer().getLocation().getY() - e.getPlayer().getLocation().getBlockY() == 0) {
                Bukkit.getScheduler().runTaskLater(Runner.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        l.getBlock().setData((byte) 14);
                    }
                }, 5L);

                Bukkit.getScheduler().runTaskLater(Runner.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        l.getBlock().getLocation().getWorld().spawnFallingBlock(l.getBlock().getLocation(), l.getBlock().getType(), l.getBlock().getData());
                        l.getBlock().setType(Material.AIR);
                    }
                }, 10L);
            }
        }
    }

}
