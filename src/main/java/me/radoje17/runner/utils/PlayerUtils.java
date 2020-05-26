package me.radoje17.runner.utils;

import me.radoje17.runner.Runner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerUtils {

    private static File f;
    private static FileConfiguration playerData;

    public PlayerUtils() {
        this.f = new File(Runner.getInstance().getDataFolder(), "playerData.yml");
        reloadConfig();
    }

    public static void reloadConfig() {
        playerData = YamlConfiguration.loadConfiguration(f);
    }

    private static void saveConfig() {
        try {
            playerData.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getWins(String playerName) {
        return playerData.getInt(UUIDUtils.getUUID(playerName) + ".wins");
    }

    public static int getLosses(String playerName) {
        return playerData.getInt(UUIDUtils.getUUID(playerName) + ".losses");
    }

    public static void setWins(String playerName, int wins) {
        playerData.set(UUIDUtils.getUUID(playerName) + ".wins", wins);
    }

    public static void setLosses(String playerName, int losses) {
        playerData.set(UUIDUtils.getUUID(playerName) + ".losses", losses);
    }



}
