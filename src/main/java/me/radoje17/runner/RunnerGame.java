package me.radoje17.runner;

import me.radoje17.runner.utils.ConfigUtils;
import me.radoje17.runner.utils.PlayerUtils;
import me.radoje17.runner.utils.WorldUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class RunnerGame {

    String arenaName;

    private List<Player> players;

    private List<Location> spawnPoints;

    private boolean active = false;

    private Iterator<Location> iterator;

    private int countdown = 10;

    private int taskID = -1;

    public boolean isActive() {
        return active;
    }

    public int getTaskID() {
        return taskID;
    }

    public int getCountdown() {
        return countdown;
    }

    public RunnerGame(String arenaName) {
        this.arenaName = arenaName;

        this.players = new ArrayList<>();
        this.spawnPoints = new ArrayList<>();

        RunnerGameManager runnerGameManager = Runner.getInstance().getRunnerGameManager();
        World runnerWorld = Runner.getRunnerWorld();


        int x = runnerGameManager.x;
        int z = runnerGameManager.z;

        runnerGameManager.x+=400;
        runnerGameManager.z+=400;

        /*Bukkit.getScheduler().runTaskAsynchronously(Runner.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {*/
                    WorldUtils.pasteSchematic(arenaName, runnerWorld, x, z);

                    spawnPoints = WorldUtils.loadSpawnpoints(runnerWorld, x, z);
                    iterator = spawnPoints.iterator();
                /*} catch (Exception e) {
                    System.out.println(e.getCause());
                    System.out.println(e.getLocalizedMessage());
                }

            }
        });*/

    }

    public void startCountdown(){
        taskID = Bukkit.getScheduler().runTaskTimer(Runner.getInstance(), new Runnable() {
            @Override
            public void run() {
                countdown-=1;

                if (countdown == 0) {
                    active = true;
                    Runner.getInstance().getRunnerGameManager().activeRunnerGameList.add(RunnerGame.this);
                    Runner.getInstance().getRunnerGameManager().waitingRunnerGameList.remove(RunnerGame.this);
                    Runner.getInstance().getRunnerGameManager().getGame(arenaName);

                    Collections.shuffle(spawnPoints);
                    Iterator<Location> iter = spawnPoints.iterator();

                    for (Player p : players) {
                        p.sendMessage("Igra pocinje!");
                        Location l = iter.next();
                        p.teleport(l);
                        l.setY(l.getY()-1);
                        Bukkit.getScheduler().runTaskLater(Runner.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                l.getBlock().setData((byte) 14);
                            }
                        }, 2L);

                        Bukkit.getScheduler().runTaskLater(Runner.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                l.getBlock().setType(Material.AIR);
                            }
                        }, 7L);
                        Runner.getInstance().getRunnerGameManager().waitingPlayers.remove(p);
                        Runner.getInstance().getRunnerGameManager().activePlayers.put(p, RunnerGame.this);

                    }



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
            if (p.getGameMode() == GameMode.SPECTATOR) {
                i++;
            }
        }

        return players.size() - i;
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
            p.teleport(ConfigUtils.getLobby());
        }
    }

    public void addPlayer(Player p) {
        if (iterator != null && iterator.hasNext()) {
            Runner.getInstance().getRunnerGameManager().waitingPlayers.put(p, this);
            Runner.getInstance().getRunnerGameManager().inventories.put(p, p.getInventory().getContents());
            p.getInventory().clear();
            p.setGameMode(GameMode.ADVENTURE);
            players.add(p);
            p.teleport(iterator.next());
            p.setHealth(20);
            p.setFoodLevel(20);
            p.sendMessage("Usao si na arenu: " + arenaName);
        }

        if (players.size() == 2) {
            startCountdown();
        }
    }

    public void endGame() {
        Player winner = getWinner();
        PlayerUtils.addWin(winner.getName());
        for (Player p : players) {
            if (p != winner) {
                PlayerUtils.addLoss(p.getName());
            }
            p.setGameMode(GameMode.SPECTATOR);
            p.teleport(ConfigUtils.getLobby());
            if (Runner.getInstance().getRunnerGameManager().inventories.containsKey(p)) {
                p.getInventory().setContents(Runner.getInstance().getRunnerGameManager().inventories.get(p));
            }
            p.setGameMode(GameMode.ADVENTURE);
            Runner.getInstance().getRunnerGameManager().activePlayers.remove(p);
            p.sendMessage(winner.getName() + " je pobedio!");
        }

        players.clear();
        Runner.getInstance().getRunnerGameManager().activeRunnerGameList.remove(this);
    }

    public void removePlayer(Player p) {
        players.remove(p);
        if (active) {
            Runner.getInstance().getRunnerGameManager().activePlayers.remove(p);
            p.sendMessage(p.getName() + " left.");
            if (getPlayerCount() == 1) {
                endGame();
            } else {
                PlayerUtils.addLoss(p.getName());
            }
        } else {
            Runner.getInstance().getRunnerGameManager().waitingPlayers.remove(p);
            if (players.size() < 2) {
                for (Player player : players) {
                    player.sendMessage("Nema dovoljno igraca da zopocnemo igru!");
                    Bukkit.getScheduler().cancelTask(taskID);
                    this.countdown = 10;
                    taskID = -1;
                }
            }
        }
        p.setGameMode(GameMode.SPECTATOR);
        p.teleport(ConfigUtils.getLobby());
        if (Runner.getInstance().getRunnerGameManager().inventories.containsKey(p)) {
            p.getInventory().setContents(Runner.getInstance().getRunnerGameManager().inventories.get(p));
        }
        p.setGameMode(GameMode.ADVENTURE);

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
