package org.elbukkit.crowdcontrol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.elbukkit.crowdcontrol.entity.CreatureType;
import org.elbukkit.crowdcontrol.entity.EnderDragon;
import org.elbukkit.crowdcontrol.entity.EntityData;
import org.elbukkit.crowdcontrol.entity.EntityInstance;
import org.elbukkit.crowdcontrol.entity.Nature;
import org.elbukkit.crowdcontrol.settings.MasterSettings;

public class CreatureControl implements Runnable {

    CrowdControlPlugin plugin;
    MasterSettings settings;
    Map<LivingEntity, EntityInstance> masterList = new HashMap<LivingEntity, EntityInstance>();

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

            // Burn code, kill code
            for (LivingEntity e : w.getLivingEntities()) {
                if (e instanceof Player) {
                    continue;
                }

                EntityInstance instance = getInstance(e);
                CreatureType type = CreatureType.creatureTypeFromEntity(e);
                EntityData data = plugin.getSettingManager().getSetting(type, w);

                // Burn code, and nature code
                if (e.getLocation().getBlock().getLightFromSky() > 7) {
                    if (data.isBurnDay()) {
                        e.setFireTicks(20);
                    }
                    instance.setNature(data.getNatureDay());
                } else {
                    instance.setNature(data.getNatureNight());
                }

                // Kill code
                if (instance.isDead()) {
                    if (e.getKiller() != null) {
                        e.damage(e.getMaxHealth(), e.getKiller());
                    }
                    e.damage(e.getMaxHealth());
                }

                // Targeting code
                List<Entity> nearbyTargets = e.getNearbyEntities(data.getTargetDistance(), data.getTargetDistance(), data.getTargetDistance());
                for (Entity ne : nearbyTargets) {
                    if (ne instanceof Player) {
                        if (instance.getCurentNature() == Nature.AGGRESSIVE) {
                            if (e instanceof Creature) {
                                if (((Creature) e).getTarget() != null) {
                                    continue;
                                }
                                ((Creature) e).setTarget((LivingEntity) ne);
                            }
                        }
                    }
                }

                // Don't want to despawn the boss
                if (e instanceof EnderDragon) {
                    if (e.getWorld().getEnvironment() == Environment.THE_END) {
                        continue;
                    }
                }

                List<Entity> nearbyEntities = e.getNearbyEntities(settings.getDespawnRadius(), 20, settings.getDespawnRadius());
                boolean playerNearby = false;
                for (Entity ne : nearbyEntities) {
                    if (ne instanceof Player) {
                        playerNearby = true;
                    }
                }

                if (playerNearby) {
                    continue;
                }

                if (new Random().nextDouble() >= settings.getDespawnChance()) {
                    masterList.remove(e);
                    e.remove();
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

                        if ((testBlock.getType() != Material.AIR) && (testBlock.getType() != Material.WATER)) {
                            continue;
                        }

                        if (p.getLocation().distance(testBlock.getLocation()) < settings.getNoSpawnRadius()) {
                            continue;
                        }

                        if (data.canSpawn(testBlock)) {
                            LivingEntity e = w.spawnCreature(testBlock.getLocation(), randomType.toBukkitType());
                            masterList.put(e, new EntityInstance(data, e));
                        }

                    }
                }
            }
        }
    }

    public EntityInstance getInstance(LivingEntity e) {

        if (!masterList.containsKey(e)) {
            CreatureType type = CreatureType.creatureTypeFromEntity(e);
            EntityData data = plugin.getSettingManager().getSetting(type, e.getWorld());
            masterList.put(e, new EntityInstance(data, e));
        }

        return masterList.get(e);
    }

    public void removeEntity(LivingEntity e) {
        masterList.remove(e);
    }
}
