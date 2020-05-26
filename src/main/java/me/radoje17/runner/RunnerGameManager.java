package me.radoje17.runner;

import me.radoje17.runner.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
        if (isInGame(e.getPlayer())) {
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

                return;
            }

            if (e.getPlayer().getLocation().getY() < 0) {
                game.messageAllPlayers("Igrac " + e.getPlayer().getName() + " je eleminisan");

                if (game.getPlayerCount()-1 == 1) {
                    game.endGame();
                    return;
                }

                e.getPlayer().setGameMode(GameMode.SPECTATOR);
                Location loc = game.getRandomSpawnPoint();
                loc.setY(loc.getY() + 20);
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

            if (game.getPlayerCount()-1 == 1) {
                game.endGame();
                return;
            }

            player.setGameMode(GameMode.SPECTATOR);
            Location loc = game.getRandomSpawnPoint();
            loc.setY(loc.getY() + 20);
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
    public void damage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && isWaitingOrInGame((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

}
