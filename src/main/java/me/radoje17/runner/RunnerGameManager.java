package me.radoje17.runner;

import me.radoje17.runner.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RunnerGameManager implements Listener {

    public int x = 0, z = 0;

    List<RunnerGame> activeRunnerGameList;
    List<RunnerGame> waitingRunnerGameList;

    HashMap<Player, RunnerGame> activePlayers;
    HashMap<Player, RunnerGame> waitingPlayers;

    HashMap<Player, ItemStack[]> inventories;

    public RunnerGameManager() {
        this.activeRunnerGameList = new ArrayList<>();
        this.waitingRunnerGameList = new ArrayList<>();
        this.activePlayers = new HashMap<>();
        this.waitingPlayers = new HashMap<>();

        this.inventories = new HashMap<>();
    }

    public List<RunnerGame> getActiveRunnerGameListCopy() {
        return new ArrayList<>(activeRunnerGameList);
    }

    public List<RunnerGame> getWaitingRunnerGameListCopy() {
        return new ArrayList<>(waitingRunnerGameList);
    }

    public void forceStopAllGames() {
        for (RunnerGame g : waitingRunnerGameList) {
            g.forceRemoveAllPlayers();
        }

        for (RunnerGame g : activeRunnerGameList) {
            g.forceRemoveAllPlayers();
        }
    }

    public RunnerGame getGame(Player p) {
        if (isWaiting(p)) {
            return waitingPlayers.get(p);
        }
        if (isInGame(p)) {
            return activePlayers.get(p);
        }
        return null;
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
        if (isWaiting(e.getPlayer()) && e.getPlayer().getLocation().getY() < 0) {
            RunnerGame game = waitingPlayers.get(e.getPlayer());
            e.getPlayer().teleport(game.getRandomSpawnPoint());
        }

        if (isInGame(e.getPlayer())) {
            RunnerGame game = activePlayers.get(e.getPlayer());

            Location l = e.getPlayer().getLocation();
            l.setY(l.getY()-1);

            List<Block> toRemove = new ArrayList<Block>();

            toRemove.add(l.getBlock());

            // Ovo ovde je da proveri da li je igrac na ivici i ako jeste dodaje taj blok

            if (l.getX() - l.getBlockX() <= 0.3) {
                toRemove.add(l.getWorld().getBlockAt(l.getBlockX()-1, l.getBlockY(), l.getBlockZ()));
            }

            if (l.getX() - l.getBlockX() >= 0.7) {
                toRemove.add(l.getWorld().getBlockAt(l.getBlockX()+1, l.getBlockY(), l.getBlockZ()));
            }

            if (l.getZ() - l.getBlockZ() <= 0.3) {
                toRemove.add(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()-1));
            }

            if (l.getZ() - l.getBlockZ() >= 0.7) {
                toRemove.add(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()+1));
            }

            // ----------

            if (l.getX() - l.getBlockX() <= 0.3 && l.getZ() - l.getBlockZ() <= 0.3) {
                toRemove.add(l.getWorld().getBlockAt(l.getBlockX()-1, l.getBlockY(), l.getBlockZ()-1));
            }

            if (l.getX() - l.getBlockX() >= 0.7 && l.getZ() - l.getBlockZ() >= 0.7) {
                toRemove.add(l.getWorld().getBlockAt(l.getBlockX()+1, l.getBlockY(), l.getBlockZ()+1));
            }

            if (l.getX() - l.getBlockX() >= 0.7 && l.getZ() - l.getBlockZ() <= 0.3) {
                toRemove.add(l.getWorld().getBlockAt(l.getBlockX()+1, l.getBlockY(), l.getBlockZ()-1));
            }

            if (l.getX() - l.getBlockX() <= 0.3 && l.getZ() - l.getBlockZ() >= 0.7) {
                toRemove.add(l.getWorld().getBlockAt(l.getBlockX()-1, l.getBlockY(), l.getBlockZ()+1));
            }

            if (e.getPlayer().getLocation().getY() - e.getPlayer().getLocation().getBlockY() == 0) {
                /*Bukkit.getScheduler().runTaskLater(Runner.getInstance(), new Runnable() {
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
                }, 7L);*/

                Bukkit.getScheduler().runTaskLater(Runner.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        for (Block b : toRemove)
                            b.setData((byte) 14);
                    }
                }, 2L);

                Bukkit.getScheduler().runTaskLater(Runner.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        for (Block b : toRemove)
                            b.setType(Material.AIR);
                    }
                }, 7L);

                return;
            }

            if (e.getPlayer().getLocation().getY() < 0) {
                game.messageAllPlayers("Igrac " + e.getPlayer().getName() + " je eleminisan");

                e.getPlayer().setGameMode(GameMode.SPECTATOR);
                if (game.getPlayerCount() == 1) {
                    game.endGame();
                    return;
                }


                Location loc = game.getRandomSpawnPoint();
                loc.setY(loc.getY());
                e.getPlayer().teleport(loc);
            }
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        if (isInGame(e.getEntity())) {
            e.setDeathMessage("");
            Player player = (Player) e.getEntity();

            RunnerGame game = activePlayers.get(player);


            game.messageAllPlayers("Igrac " + player.getName() + " je eleminisan");

            player.setGameMode(GameMode.SPECTATOR);
            if (game.getPlayerCount() == 1) {
                game.endGame();
                return;
            }

            player.setGameMode(GameMode.SPECTATOR);
            Location loc = game.getRandomSpawnPoint();
            loc.setY(loc.getY());
            player.teleport(loc);



            Bukkit.getScheduler().runTaskLater(Runner.getInstance(), new Runnable() {
                @Override
                public void run() {
                    player.spigot().respawn();
                }
            }, 1L);

        }
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player && isWaitingOrInGame((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && isWaitingOrInGame((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void commands(PlayerCommandPreprocessEvent e) {
        if (isWaitingOrInGame(e.getPlayer()) && !e.getMessage().equalsIgnoreCase("/leave")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if (!isWaitingOrInGame(e.getPlayer())) {
            return;
        }

        RunnerGame game = getGame(e.getPlayer());
        if (game != null)
            game.removePlayer(e.getPlayer());
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
        e.getPlayer().getInventory().clear();
        e.getPlayer().teleport(ConfigUtils.getLobby());
    }

    @EventHandler
    public void spawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof Player) && e.getLocation().getWorld() == Runner.getRunnerWorld())
            e.setCancelled(true);
    }

}
