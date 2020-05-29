package me.radoje17.runner.utils;

        import me.radoje17.runner.Runner;
        import org.bukkit.Bukkit;
        import org.bukkit.Location;
        import org.bukkit.configuration.file.FileConfiguration;

        import java.io.File;
        import java.util.ArrayList;
        import java.util.List;

public class ConfigUtils {

   /* private File f;
    private FileConfiguration config;

    public ConfigUtils() {
        this.f = new File(Runner.getInstance().getDataFolder() + "/config.yml");
    }*/

    public static Location getLobby() {
        return Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    public static List<String> getArenaList() {
        List<String> arenas = new ArrayList<>();
        for (File f : new File(Runner.getInstance().getDataFolder() + "/schematics").listFiles()) {
            try {
                arenas.add(f.getName().split(".schematic")[0]);
            } catch (Exception e) {
                System.out.println(f.getName() + " nije schematic!");
            }
        }
        return arenas;
    }


}
