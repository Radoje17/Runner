package me.radoje17.runner;

import me.radoje17.runner.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RunnerGame {

    String arenaName;

    private List<Player> players;

    private List<Location> spawnPoints;

    private boolean active = false;

    private World world;

    private Iterator<Location> iterator;

    int countdown = 10;

    int taskID;

    public RunnerGame(String arenaName) {
        this.arenaName = arenaName;

        this.players = new ArrayList<>();
        this.spawnPoints = new ArrayList<>();

        world = WorldUtils.createWorld(arenaName + ".schematic");

        spawnPoints = WorldUtils.loadSpawnpoints(world);
        iterator = spawnPoints.iterator();
    }

    public void startCountdown(){
        taskID = Bukkit.getScheduler().runTaskTimer(Runner.getInstance(), new Runnable() {
            @Override
            public void run() {
                countdown-=1;

                if (countdown == 0) {

                    Iterator<Location> iter = spawnPoints.iterator();
                    for (Player p : players) {
                        p.sendMessage("Igra pocinje!");

                        Runner.getInstance().getRunnerGameManager().waitingPlayers.remove(p);
                        Runner.getInstance().getRunnerGameManager().activePlayers.put(p, RunnerGame.this);

                        if (iter.hasNext()) {
                            p.teleport(iter.next());
                        } else {
                            // Uradi nesto ruzno >:(
                        }
                    }

                    Runner.getInstance().getRunnerGameManager().waitingRunnerGameList.remove(RunnerGame.this);
                    Runner.getInstance().getRunnerGameManager().activeRunnerGameList.add(RunnerGame.this);

                    Bukkit.getScheduler().cancelTask(taskID);
                    return;
                }

                for (Player p : players) {
                    p.sendMessage("Igra pocinje za " + countdown + "s");
                }
            }

        }, 20L, 20L).getTaskId();
    }


    public int getPlayerCount() {
        return players.size();
    }

    public void forceRemoveAllPlayers() {
        for (Player p : players) {
            // stavi na lobby posle
            p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        WorldUtils.deleteWorld(world);
    }

    public void addPlayer(Player p) {
        if (iterator != null && iterator.hasNext()) {
            players.add(p);
            p.teleport(iterator.next());
            p.sendMessage("Usao si na arenu: " + arenaName);
        }

        if (players.size() == 2) {
            startCountdown();
        }
        // Teleport, clear inventory, save old inventory
    }

    public void removePlayer(Player p) {
        players.remove(p);

        // Give stuff back, teleport back to lobby
    }


    public String getArenaName() {
        return arenaName;
    }

    public int getSlots() {
        return spawnPoints.size();
    }

}
