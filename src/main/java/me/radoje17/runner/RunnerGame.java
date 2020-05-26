package me.radoje17.runner;

import me.radoje17.runner.utils.ConfigUtils;
import me.radoje17.runner.utils.WorldUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
                            iter = spawnPoints.iterator();
                            p.teleport(iter.next());
                        }
                    }

                    Runner.getInstance().getRunnerGameManager().waitingRunnerGameList.remove(RunnerGame.this);
                    Runner.getInstance().getRunnerGameManager().activeRunnerGameList.add(RunnerGame.this);

                    active = true;
                    Bukkit.getScheduler().cancelTask(taskID);
                    return;
                }

                for (Player p : players) {
                    p.sendMessage("Igra pocinje za " + countdown + "s");
                }
            }

        }, 20L, 20L).getTaskId();
    }

    public Location getRandomSpawnPoint() {
        return spawnPoints.get(new Random().nextInt(spawnPoints.size()));
    }

    public int getPlayerCount() {
        int i = 0;
        for (Player p : players) {
            if (p.getGameMode() == GameMode.ADVENTURE) {
                i++;
            }
        }
        return i;
    }

    public Player getWinner() {
        for (Player p : players) {
            if (p.getGameMode() == GameMode.ADVENTURE) {
                return p;
            }
        }

        return null;
    }

    public void forceRemoveAllPlayers() {
        for (Player p : players) {
            // stavi na lobby posle
            p.teleport(ConfigUtils.getLobby());
        }
        WorldUtils.deleteWorld(world);
    }

    public void addPlayer(Player p) {
        if (iterator != null && iterator.hasNext()) {
            p.setGameMode(GameMode.ADVENTURE);
            players.add(p);
            p.teleport(iterator.next());
            p.sendMessage("Usao si na arenu: " + arenaName);
        }

        if (players.size() == 2) {
            startCountdown();
        }
        // Teleport, clear inventory, save old inventory
    }

    public void endGame() {

        Player winner = getWinner();

        for (Player p : players) {
            p.setGameMode(GameMode.ADVENTURE);
            p.teleport(ConfigUtils.getLobby());
            Runner.getInstance().getRunnerGameManager().activePlayers.remove(p);
            p.sendMessage(winner.getName() + " je pobedio!");
        }

        players.clear();
        Runner.getInstance().getRunnerGameManager().activeRunnerGameList.remove(this);
        WorldUtils.deleteWorld(world);
    }

    public void removePlayer(Player p) {
        players.remove(p);

        if (active) {
            Runner.getInstance().getRunnerGameManager().activePlayers.remove(p);
            if (p.getGameMode() != GameMode.SPECTATOR) {
                p.sendMessage(p.getName() + " left.");
            }

        } else {
            Runner.getInstance().getRunnerGameManager().waitingPlayers.remove(p);
            if (players.size() < 2) {
                for (Player player : players) {
                    player.sendMessage("Nema dovoljno igraca da zopocnemo igru!");
                    Bukkit.getScheduler().cancelTask(taskID);
                    this.countdown = 30;
                }
            }
        }

        p.setGameMode(GameMode.ADVENTURE);
        p.teleport(ConfigUtils.getLobby());

        // Give stuff back, teleport back to lobby
    }


    public String getArenaName() {
        return arenaName;
    }

    public int getSlots() {
        return spawnPoints.size();
    }

    public void messageAllPlayers(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        for (Player p : players) {
            p.sendMessage(message);
        }
    }

}
