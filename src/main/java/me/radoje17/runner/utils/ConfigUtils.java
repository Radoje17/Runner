package me.radoje17.runner.utils;

import me.radoje17.runner.Runner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigUtils {

   /* private File f;
    private FileConfiguration config;

    public ConfigUtils() {
        this.f = new File(Runner.getInstance().getDataFolder() + "/config.yml");
    }*/

    public static Location getLobby() {
        return Bukkit.getWorlds().get(0).getSpawnLocation();
    }

}
