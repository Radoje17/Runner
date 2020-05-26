package me.radoje17.runner.utils;

import me.radoje17.runner.Runner;
import me.radoje17.runner.RunnerGame;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;

public final class UUIDUtils implements Listener {

    private static File f;
    private static FileConfiguration data;

    public UUIDUtils() {
        Bukkit.getPluginManager().registerEvents(this, Runner.getInstance());
        refreshData();
    }

    private void refreshData() {
        this.f = new File(Bukkit.getWorldContainer() + "/uuids.yml");
        data = YamlConfiguration.loadConfiguration(f);
    }

    private void saveData() {
        try {
            data.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUUID(String player) {
        player = player.toLowerCase();
        f = new File(Bukkit.getWorldContainer() + "/uuids.yml");
        data = YamlConfiguration.loadConfiguration(f);
        return data.getString("uuids." + player);
    }

    public static String getUUID(Player player) {
        f = new File(Bukkit.getWorldContainer() + "/uuids.yml");
        data = YamlConfiguration.loadConfiguration(f);
        return data.getString("uuids." + player.getName().toLowerCase());
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        data.set("uuids." + e.getPlayer().getName().toLowerCase(), e.getPlayer().getUniqueId().toString());
        saveData();
    }

}
