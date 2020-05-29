package me.radoje17.runner.utils;

import me.radoje17.runner.Runner;
import me.radoje17.runner.RunnerGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InventoryUtils implements Listener {

    private static Inventory mapsInventory;

    public InventoryUtils() {
        mapsInventory = Bukkit.createInventory(null, 9*6, "Runner igre");

        Bukkit.getScheduler().runTaskTimerAsynchronously(Runner.getInstance(), new Runnable() {
            @Override
            public void run() {
                updateInventory();
            }
        }, 20L, 20L);
    }

    private void updateInventory() {

        mapsInventory.clear();

        List<RunnerGame> waitingGames = Runner.getInstance().getRunnerGameManager().getWaitingRunnerGameListCopy();

        Collections.sort(waitingGames, new Comparator<RunnerGame>() {
            @Override
            public int compare(RunnerGame game1, RunnerGame game2) {
                return game1.getArenaName().compareTo(game2.getArenaName());
            }
        });

        for (RunnerGame g : waitingGames) {
            mapsInventory.addItem(runnerGameToItemStack(g));
        }

        List<RunnerGame> activeGames = Runner.getInstance().getRunnerGameManager().getActiveRunnerGameListCopy();

        Collections.sort(activeGames, new Comparator<RunnerGame>() {
            @Override
            public int compare(RunnerGame game1, RunnerGame game2) {
                return game1.getArenaName().compareTo(game2.getArenaName());
            }
        });

        for (RunnerGame g : activeGames) {
            mapsInventory.addItem(runnerGameToItemStack(g));
        }
    }

    public static Inventory getMapsInventory() {
        return mapsInventory;
    }

    private ItemStack runnerGameToItemStack(RunnerGame g) {
        ItemStack itemStack = null;
        List<String> lore = new ArrayList<>();
        String displayName = g.getArenaName();

        if (g.isActive()) {
            itemStack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
            displayName = "&c" + displayName + " &7(&e"+g.getPlayerCount() + "&7/&e" + g.getSlots() + "&7)";
            lore.add(ChatColor.translateAlternateColorCodes('&', "&cIgra u toku"));
        } else if (g.getTaskID() != -1) {
           itemStack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 4);
            displayName = "&e" + displayName + " &7(&e"+g.getPlayerCount() + "&7/&e" + g.getSlots() + "&7)";
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Igra pocinje za &e" + g.getCountdown() + "s"));
        } else {
            itemStack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
            displayName = "&e" + displayName + " &7(&e"+g.getPlayerCount() + "&7/&e" + g.getSlots() + "&7)";
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cekaju se igraci"));
        }

        displayName = ChatColor.translateAlternateColorCodes('&', displayName);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }


    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equals("Runner igre") && e.getCurrentItem() != null) {
            e.setCancelled(true);

            if (e.getCurrentItem().getData().getData() == (byte) 4 || e.getCurrentItem().getData().getData() == (byte) 5) {
                Runner.getInstance().getRunnerGameManager().getGame(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().split(" ")[0])).addPlayer((Player) e.getWhoClicked());
                e.getWhoClicked().closeInventory();
            }
        }
    }

}
