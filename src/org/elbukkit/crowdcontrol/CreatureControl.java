package org.elbukkit.crowdcontrol;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.elbukkit.crowdcontrol.entity.CreatureType;
import org.elbukkit.crowdcontrol.entity.EntityData;
import org.elbukkit.crowdcontrol.settings.MasterSettings;

public class CreatureControl implements Runnable {

    CrowdControlPlugin plugin;
    MasterSettings settings;

    public CreatureControl(CrowdControlPlugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettingManager().getMasterSettings();
    }

    @Override
    public void run() {

        for (World w : Bukkit.getWorlds()) {
            
            if (!plugin.getSettingManager().getMasterSettings().isEnabledWorld(w)) {
                continue;
            }
            
            List<Player> players = w.getPlayers();
            
            if (players.size() <= 0) {
                continue;
            }
            
            if (settings.getMaxPerWorld() != -1) {
                if (w.getLivingEntities().size() >= settings.getMaxPerWorld()) {
                    continue;
                }
            }

            // Burn code
            for (LivingEntity e : w.getLivingEntities()) {
                if (e instanceof Player)
                    continue;
                CreatureType type = CreatureType.creatureTypeFromEntity(e);
                EntityData data = plugin.getSettingManager().getSetting(type, w);

                if (isDay(e.getWorld())) {
                    if (e.getLocation().getBlock().getLightFromSky() > 7) {
                        if (data.isBurnDay()) {
                            e.setFireTicks(20);
                        }
                    }
                }
            }

            // Spawn an creature by each player
            for (Player p : players) {
                for (int i = 0; i < new Random().nextInt(64); i++) {
                    CreatureType randomType = CreatureType.values()[new Random().nextInt(CreatureType.values().length)];
                    EntityData data = plugin.getSettingManager().getSetting(randomType, w);

                    HashSet<Chunk> spawningChunks = new HashSet<Chunk>();
                    Chunk playerChunk = w.getChunkAt(p.getLocation());
                    for (int x = playerChunk.getX() - settings.getChunkRadius(); x < (playerChunk.getX() + settings.getChunkRadius()); x++) {
                        for (int z = playerChunk.getZ() - settings.getChunkRadius(); z < (playerChunk.getZ() + settings.getChunkRadius()); z++) {
                            spawningChunks.add(w.getChunkAt(x, z));
                        }
                    }

                    for (Chunk c : spawningChunks) {

                        Random r = new Random();

                        if (settings.getMaxPerChunk() != -1) {
                            if (c.getEntities().length >= settings.getMaxPerChunk()) {
                                continue;
                            }
                        }

                        Block testBlock = c.getBlock(r.nextInt(16), r.nextInt(128), r.nextInt(16));
                        
                        if (testBlock.getType() != Material.AIR && testBlock.getType() != Material.WATER) {
                            continue;
                        }

                        if(p.getLocation().distance(testBlock.getLocation()) < settings.getNoSpawnRadius()){
                            continue;
                        }

                        if (data.canSpawn(testBlock)) {
                            w.spawnCreature(testBlock.getLocation(), randomType.toBukkitType());
                        }

                    }
                }
            }
        }
    }

    public boolean isDay(World w) {
        if ((w.getTime() > 0) && (w.getTime() < 12000)) {
            return true;
        }
        return false;
    }
}
