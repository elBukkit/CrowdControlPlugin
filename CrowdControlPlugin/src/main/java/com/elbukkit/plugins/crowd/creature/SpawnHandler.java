package com.elbukkit.plugins.crowd.creature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;
import com.elbukkit.plugins.crowd.events.CreatureSpawnEvent;
import com.elbukkit.plugins.crowd.events.CrowdListener;
import com.elbukkit.plugins.crowd.rules.Type;

/**
 * This is the main spawner, thanks to the obfuscated minecraft code for helping
 * me
 * 
 * @author WinSock
 * @version 1.0
 */
public class SpawnHandler implements Runnable {
    
    private final CreatureHandler    handler;
    private final CrowdControlPlugin plugin;
    private final Random             rand = new Random();
    private final World              world;
    
    public SpawnHandler(final CrowdControlPlugin plugin, final CreatureHandler handler) {
        this.plugin = plugin;
        this.world = handler.getWorld();
        this.handler = handler;
    }
    
    private Block getRandomSpawningPointInChunk(final Chunk c) {
        final int x = c.getX() + this.rand.nextInt(16);
        final int y = this.rand.nextInt(128);
        final int z = c.getZ() + this.rand.nextInt(16);
        return c.getBlock(x, y, z);
    }
    
    public void run() {
        
        final List<CreatureType> enabledTypes = new ArrayList<CreatureType>(this.handler.getEnabledCreatureTypes());
        if ((enabledTypes.size() > 0) && (this.world.getPlayers().size() > 0)) {
            
            final List<Chunk> spawningChunks = new ArrayList<Chunk>();
            
            for (final Player p : this.world.getPlayers()) {
                
                final int pChunkX = (int) Math.floor(p.getLocation().getX() / 16.0D);
                final int pChunkZ = (int) Math.floor(p.getLocation().getZ() / 16.0D);
                
                for (int x = -8; x <= 8; x++) {
                    for (int z = -8; z <= 8; z++) {
                        spawningChunks.add(this.world.getChunkAt(x + pChunkX, z + pChunkZ));
                    }
                }
            }
            
            Collections.shuffle(spawningChunks, this.rand); // Randomize the list
            
            for (int i = 0; i <= 3; i++) { // 4 chances to spawn
            
                for (final Chunk c : spawningChunks) {
                    if (this.handler.getCreatureCount() < this.plugin.getMaxPerWorld()) {
                        if (c.getEntities().length < this.plugin.getMaxPerChunk()) {
                            
                            final CreatureType type = enabledTypes.get(this.rand.nextInt(enabledTypes.size()));
                            
                            final Block spawnBlock = this.getRandomSpawningPointInChunk(c);
                            
                            if ((spawnBlock.getType() == Material.AIR) || (spawnBlock.getType() == Material.WATER) || (spawnBlock.getType() == Material.STATIONARY_WATER)) {
                                if (this.world.getBlockAt(spawnBlock.getX(), spawnBlock.getY() - 1, spawnBlock.getZ()).getType() != Material.AIR) {
                                    final Info info = new Info(this.plugin);
                                    
                                    info.setLocation(spawnBlock.getLocation());
                                    info.setEnv(this.world.getEnvironment());
                                    info.setType(type);
                                    
                                    if (this.rand.nextFloat() < this.handler.getBaseInfo(type).getSpawnChance()) {
                                        
                                        for (final Player p : this.world.getPlayers()) {
                                            final double deltax = Math.abs(spawnBlock.getLocation().getX() - p.getLocation().getX());
                                            final double deltay = Math.abs(spawnBlock.getLocation().getY() - p.getLocation().getY());
                                            final double deltaz = Math.abs(spawnBlock.getLocation().getZ() - p.getLocation().getZ());
                                            final double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));
                                            
                                            if (distance < this.plugin.getMinDistanceFromPlayer()) {
                                                continue;
                                            }
                                        }
                                        
                                        if (this.plugin.getRuleHandler(spawnBlock.getWorld()).passesRules(info, Type.SPAWN)) {
                                            final CreatureSpawnEvent event = new CreatureSpawnEvent(this, info.getLocation(), info.getType());
                                            
                                            for (final CrowdListener cListener : this.plugin.getListeners()) {
                                                cListener.onCreatureSpawn(event);
                                            }
                                            
                                            if (!event.isCancelled()) {
                                                info.spawn();
                                            }
                                        }
                                        
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
    }
    
}