package me.radoje17.runner.utils;

import com.boydti.fawe.Fawe;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import me.radoje17.runner.Runner;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class WorldUtils {

    public static List<Location> loadSpawnpoints(World world, int x_add, int z_add) {
        int radius = 200;

        List<Location> spawnpoints = new ArrayList<>();

        for (int x = -(radius); x <= radius; x++) {
            for (int y = 0; y <= 256; y++) {
                for (int z = -(radius); z <= radius; z++) {
                    if (world.getBlockAt(x+x_add, y, z+z_add).getType().toString().contains("SIGN")) {
                        Location l = new Location(world, x+x_add, y, z+z_add);

                        l.getBlock().setType(Material.AIR);

                        double x1 = x + x_add + 0.5, z1 = z + z_add + 0.5;

                        spawnpoints.add(new Location(world, x1, y, z1));
                        continue;
                    }
                }
            }
        }

        return spawnpoints;
    }


    public static World createWorld(String name/*String schematicName*/) {
        WorldCreator creator = new WorldCreator(name);
        creator.generator(new ChunkGenerator() {
            public List<BlockPopulator> getDefaultPopulators(World world) {
                return Arrays.asList(new BlockPopulator[0]);
            }

            public boolean canSpawn(World world, int x, int z) {
                return true;
            }

            public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
                return new byte[32768];
            }

            public Location getFixedSpawnLocation(World world, Random random) {
                return new Location(world, 0, 80, 0);
            }

        });
        World w = Bukkit.createWorld(creator);
        w.setTime(2000);
        w.setGameRuleValue("doDaylightCycle", "false");
        //BukkitWorld arenaWorld = new BukkitWorld(w);
        //pasteSchematic(new File(Runner.getInstance().getDataFolder() + "/schematics/" + schematicName), arenaWorld);
        return w;
    }

    public static EditSession pasteSchematic(String arena, World world, int x, int z) {
        try {
            File file = new File(Runner.getInstance().getDataFolder() + "/schematics/" + arena + ".schematic");
            com.sk89q.worldedit.world.World weWorld = new BukkitWorld(world);
            Vector to = new Vector(x, 80, z); // Where you want to paste

            Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(file))
                    .read((com.sk89q.worldedit.world.registry.WorldData) weWorld.getWorldData());
            // Region region = clipboard.getRegion();

            EditSession extent = new EditSessionBuilder(weWorld).fastmode(true).build();
            AffineTransform transform = new AffineTransform();

            ForwardExtentCopy copy = new ForwardExtentCopy(clipboard, clipboard.getRegion(), clipboard.getOrigin(),
                    extent, to);
            if (!transform.isIdentity())
                copy.setTransform(transform);
            copy.setSourceMask(new ExistingBlockMask(clipboard));
            Operations.complete(copy);
            extent.flushQueue();

            return extent;
        } catch (Exception e) {
            e.printStackTrace();
            Runner.getInstance().getLogger().info(
                    "Error occured whlist pasting schematic on " + world.getName() + ": " + e.getLocalizedMessage());
        }

        return null;
    }

    /*public static EditSession pasteSchematic(String arena, World world, int x, int z) {
        File f = new File(Runner.getInstance().getDataFolder() + "/schematics/" + arena + ".schematic");

        com.sk89q.worldedit.world.World weWorld = new BukkitWorld(world);


        editSession.setblock
        return ClipboardFormats.findByFile(f)..paste(world, "", false, true, (Transform) null);
        /*try {
            File file = ;
            com.sk89q.worldedit.world.World weWorld = new BukkitWorld(world);
            Vector to = new Vector(x, 80, z); // Where you want to paste

            Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(file))
                    .read((com.sk89q.worldedit.world.registry.WorldData) weWorld.getWorldData());
            // Region region = clipboard.getRegion();

            EditSession extent = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);
            AffineTransform transform = new AffineTransform();

            ForwardExtentCopy copy = new ForwardExtentCopy(clipboard, clipboard.getRegion(), clipboard.getOrigin(),
                    extent, to);
            if (!transform.isIdentity())
                copy.setTransform(transform);
            copy.setSourceMask(new ExistingBlockMask(clipboard));
            Operations.completeLegacy(copy);
            extent.flushQueue();

            return extent;
        } catch (Exception e) {
            e.printStackTrace();
            Runner.getInstance().getLogger().info(
                    "Error occured whlist pasting schematic on " + world.getName() + ": " + e.getLocalizedMessage());
        }

        return null;*/
    //}

    public static void deleteWorld(World world) {
        Bukkit.unloadWorld(world, false);
        try
        {
            FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer() + "/" + world.getName()));
        }
        catch (IOException e)
        {
            Runner.getInstance().getLogger().info("Error occured whilst deleting folder " + Bukkit.getWorldContainer() + "/" + world.getName());
        }
    }


}
