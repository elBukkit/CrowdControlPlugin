package org.elbukkit.crowdcontrol;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.elbukkit.crowdcontrol.entity.CreatureType;
import org.elbukkit.crowdcontrol.entity.EntityData;
import org.elbukkit.crowdcontrol.settings.SettingManager;

public class SpawnControl implements Runnable {

    CrowdControlPlugin plugin;
    SettingManager manager;

    public SpawnControl(CrowdControlPlugin plugin) {
        this.plugin = plugin;
        this.manager = new SettingManager(plugin);
    }

    public void run() {

        for (World w : Bukkit.getWorlds()) {
            List<Player> players = w.getPlayers();

            // Spawn an creature by each player
            for (Player p : players) {
                for (int i = 0; i < new Random().nextInt(64); i++) {
                    CreatureType randomType = CreatureType.values()[new Random().nextInt(CreatureType.values().length)];
                    EntityData data = manager.getSetting(randomType);

                    HashSet<Chunk> spawningChunks = new HashSet<Chunk>();
                    Chunk playerChunk = w.getChunkAt(p.getLocation());
                    for (int x = playerChunk.getX() - 4; x < (playerChunk.getX() + 4); x++) {
                        for (int z = playerChunk.getZ() - 4; z < (playerChunk.getZ() + 4); z++) {
                            spawningChunks.add(w.getChunkAt(x, z));
                        }
                    }

                    for (Chunk c : spawningChunks) {

                        Random r = new Random();

                        if (c.getEntities().length >= 2) {
                            continue;
                        }

                        Block testBlock = c.getBlock(r.nextInt(16), r.nextInt(128), r.nextInt(16));

                        if (testBlock.getType() != Material.AIR) {
                            continue;
                        }

                        if (data.canSpawn(testBlock)) {
                            w.spawnCreature(testBlock.getLocation(), randomType.toBukkitType());
                        }

                    }
                }
            }
            
            // Despawn code
            for (LivingEntity e : w.getLivingEntities()) {
                if (e instanceof Player) {
                    continue;
                }
                
                List<Entity> nearbyEntities = e.getNearbyEntities(20, 20, 20);
                boolean playerNearby = false;
                for (Entity ne : nearbyEntities) {
                    if(ne instanceof Player) {
                        playerNearby = true;
                    }
                }
                
                if (playerNearby) {
                    continue;
                }
                
                if (new Random().nextInt(100) == 1) {
                    e.remove();
                }
            }
        }
    }
}
