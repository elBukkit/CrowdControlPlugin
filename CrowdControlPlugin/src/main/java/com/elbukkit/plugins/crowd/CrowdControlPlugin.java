package com.elbukkit.plugins.crowd;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.elbukkit.api.elregions.elRegionsPlugin;
import com.elbukkit.plugins.crowd.creature.BaseInfo;
import com.elbukkit.plugins.crowd.creature.CreatureHandler;
import com.elbukkit.plugins.crowd.creature.CrowdCreature;
import com.elbukkit.plugins.crowd.events.CrowdListener;
import com.elbukkit.plugins.crowd.rules.MaxRule;
import com.elbukkit.plugins.crowd.rules.MovementLocationRule;
import com.elbukkit.plugins.crowd.rules.Rule;
import com.elbukkit.plugins.crowd.rules.SpawnEnvironmentRule;
import com.elbukkit.plugins.crowd.rules.SpawnHeightRule;
import com.elbukkit.plugins.crowd.rules.SpawnLightRule;
import com.elbukkit.plugins.crowd.rules.SpawnLocationRule;
import com.elbukkit.plugins.crowd.rules.SpawnMaterialRule;
import com.elbukkit.plugins.crowd.rules.SpawnReplaceRule;
import com.elbukkit.plugins.crowd.rules.SpawnTimeRule;
import com.elbukkit.plugins.crowd.rules.TargetPlayerRule;
import com.elbukkit.plugins.crowd.utils.FileUtils;
import com.elbukkit.plugins.crowd.utils.ThreadSafe;

/**
 * CrowdControl plugin
 * 
 * @author Andrew Querol(WinSock)
 * @version 0.26.05
 */
public class CrowdControlPlugin extends JavaPlugin {
    
    private static Lock                                      cHandlerLock          = new ReentrantLock();
    private static Lock                                      rHandlerLock          = new ReentrantLock();
    private Configuration                                    config;
    private ConcurrentHashMap<World, CreatureHandler>        creatureHandlers;
    private volatile int                                     despawnDistance       = 128;
    
    private elRegionsPlugin                                  elRegions;
    private final CrowdEntityListener                        entityListener        = new CrowdEntityListener(this);
    private volatile double                                  idleDespawnChance     = 0.05;
    
    private final Set<CrowdListener>                         listeners             = Collections.newSetFromMap(new ConcurrentHashMap<CrowdListener, Boolean>());
    
    private Logger                                           log;
    private volatile int                                     maxPerChunk           = 5;
    private volatile int                                     maxPerWorld           = 150;
    private volatile int                                     minDistanceFromPlayer = 10;
    private PluginDescriptionFile                            pdf;
    private ConcurrentHashMap<Class<? extends Rule>, String> ruleCommands;
    private ConcurrentHashMap<World, RuleHandler>            ruleHandlers;
    
    private volatile boolean                                 slimeSplit            = true;
    
    private volatile double                                  spiderRiderChance     = 0.01;
    private final CrowdWorldListener                         worldListener         = new CrowdWorldListener(this);
    
    /**
     * Gets a creature handler for a {@link World}
     * 
     * @param w
     *            {@link World}
     * @return {@link CreatureHandler}
     */
    @ThreadSafe
    public CreatureHandler getCreatureHandler(World w) {
        
        if (this.creatureHandlers == null) {
            return null;
        }
        
        if (this.creatureHandlers.containsKey(w)) {
            return this.creatureHandlers.get(w);
        } else {
            CreatureHandler creatureHandler = null;
            if (CrowdControlPlugin.cHandlerLock.tryLock()) {
                try {
                    creatureHandler = new CreatureHandler(w, this);
                    // Register the despawner
                    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, creatureHandler, 0, 10);
                    this.creatureHandlers.put(w, creatureHandler);
                    return creatureHandler;
                } catch (IOException e) {
                    this.log.info("[CrowdControl] Error making creature handler!");
                } finally {
                    CrowdControlPlugin.cHandlerLock.unlock();
                }
            }
            
        }
        return null;
    }
    
    /**
     * Gets the despawn distance
     * 
     * @return An {@link Integer} representing the despawn distance
     */
    @ThreadSafe
    public int getDespawnDistance() {
        return this.despawnDistance;
    }
    
    /**
     * Returns the despawn chance when idle
     * 
     * @return A {@link Double} representing the chance
     */
    @ThreadSafe
    public double getIdleDespawnChance() {
        return this.idleDespawnChance;
    }
    
    /**
     * Gets the registered listeners.
     * 
     * @return A {@link Set}<{@link CrowdListener}>
     */
    @ThreadSafe
    public Set<CrowdListener> getListeners() {
        return Collections.unmodifiableSet(this.listeners);
    }
    
    /**
     * Gets the logger
     * 
     * @return {@link Logger}
     */
    public Logger getLog() {
        return this.log;
    }
    
    /**
     * Gets the max crowd creatures per chunk
     * 
     * @return An {@link Integer} representing the max creatures per chunk
     */
    @ThreadSafe
    public int getMaxPerChunk() {
        return this.maxPerChunk;
    }
    
    /**
     * Gets the max crowd creatures per world
     * 
     * @return An {@link Integer} representing the max creatures per world
     */
    @ThreadSafe
    public int getMaxPerWorld() {
        return this.maxPerWorld;
    }
    
    /**
     * Gets how close crowd creatures can spawn to a player
     * 
     * @return A {@link Integer}
     */
    @ThreadSafe
    public int getMinDistanceFromPlayer() {
        return this.minDistanceFromPlayer;
    }
    
    /**
     * Gets the elRegions plugin
     * 
     * @return The elRegionsPlugin
     */
    public elRegionsPlugin getRegionsPlugin() {
        return this.elRegions;
    }
    
    /**
     * Gets the rule handler
     * 
     * @param w
     *            The world to get {@link RuleHandler} from
     * @return {@link RuleHandler}
     */
    public RuleHandler getRuleHandler(World w) {
        
        if (this.ruleHandlers == null) {
            return null;
        }
        
        if (this.ruleHandlers.containsKey(w)) {
            return this.ruleHandlers.get(w);
        } else {
            RuleHandler ruleHandler = null;
            if (CrowdControlPlugin.rHandlerLock.tryLock()) {
                try {
                    ruleHandler = new RuleHandler(w, this);
                    this.ruleHandlers.put(w, ruleHandler);
                    return ruleHandler;
                } catch (ClassNotFoundException e) {
                    this.log.info("Error loading rules!");
                } catch (NoSuchMethodException e) {
                    this.log.info("Error loading rules!");
                } catch (InstantiationException e) {
                    this.log.info("Error loading rules!");
                } catch (IllegalAccessException e) {
                    this.log.info("Error loading rules!");
                } catch (InvocationTargetException e) {
                    this.log.info("Error loading rules!");
                } catch (IOException e) {
                    this.log.info("Error loading rules!");
                } finally {
                    CrowdControlPlugin.rHandlerLock.unlock();
                }
            }
        }
        return null;
    }
    
    /**
     * Gets the enabled rules
     * 
     * @return The enabled rules
     */
    @ThreadSafe
    public Map<Class<? extends Rule>, String> getRules() {
        return this.ruleCommands;
    }
    
    /**
     * Gets if a slime should split.
     * 
     * @return If a slime should split.
     */
    
    public boolean getSlimeSplit() {
        return this.slimeSplit;
    }
    
    /**
     * Gets the chance of a spider spawning with a skeleton riding it
     * 
     * @return the chance 0.0 - 1.0
     */
    public double getSpiderRiderChance() {
        return this.spiderRiderChance;
    }
    
    /**
     * Loads or reloads the config file
     */
    public void loadConfigFile() {
        this.config.load();
        if (this.config.getNode("global") != null) {
            this.despawnDistance = this.config.getInt("global.despawnDistance", this.despawnDistance);
            this.idleDespawnChance = this.config.getDouble("global.idleDespawnChance", this.idleDespawnChance);
            this.maxPerChunk = this.config.getInt("global.maxPerChunk", this.maxPerChunk);
            this.maxPerWorld = this.config.getInt("global.maxPerWorld", this.maxPerWorld);
            this.minDistanceFromPlayer = this.config.getInt("global.minDistanceFromPlayer", this.minDistanceFromPlayer);
            this.slimeSplit = this.config.getBoolean("global.slimeSplit", this.slimeSplit);
            this.spiderRiderChance = this.config.getDouble("global.spiderRiderChance", this.spiderRiderChance);
        } else {
            this.config.setProperty("global.despawnDistance", this.despawnDistance);
            this.config.setProperty("global.idleDespawnChance", this.idleDespawnChance);
            this.config.setProperty("global.maxPerChunk", this.maxPerChunk);
            this.config.setProperty("global.maxPerWorld", this.maxPerWorld);
            this.config.setProperty("global.minDistanceFromPlayer", this.minDistanceFromPlayer);
            this.config.setProperty("global.slimeSplit", this.slimeSplit);
            this.config.setProperty("global.spiderRiderChance", this.spiderRiderChance);
        }
        this.config.save();
        
        if (this.creatureHandlers != null) {
            for (CreatureHandler h : this.creatureHandlers.values()) {
                h.killAll();
            }
        }
        
        this.ruleHandlers = new ConcurrentHashMap<World, RuleHandler>();
        this.creatureHandlers = new ConcurrentHashMap<World, CreatureHandler>();
        this.getServer().getScheduler().cancelTasks(this);
        
        for (World w : Bukkit.getServer().getWorlds()) {
            
            CreatureHandler cHandler = this.getCreatureHandler(w); // Create all of
            // the creature
            // handlers
            this.getRuleHandler(w); // Create the rule handlers
            
            for (LivingEntity e : w.getLivingEntities()) {
                if (!(e instanceof Player)) {
                    CreatureType cType = cHandler.getCreatureType(e);
                    BaseInfo info = cHandler.getBaseInfo(cType);
                    
                    if (info != null) {
                        cHandler.addCrowdCreature(new CrowdCreature(e, cType, info));
                    }
                }
            }
        }
    }
    
    public void onDisable() {
        this.log.info(this.pdf.getFullName() + " is disabled!");
    }
    
    public void onEnable() {
        this.pdf = this.getDescription();
        this.log = this.getServer().getLogger();
        
        this.elRegions = (elRegionsPlugin) this.getServer().getPluginManager().getPlugin("elRegions");
        
        if (this.elRegions == null) {
            this.log.info("ERROR: Could not load elRegions!");
            this.setEnabled(false);
            return;
        }
        
        this.ruleCommands = new ConcurrentHashMap<Class<? extends Rule>, String>();
        this.ruleCommands.put(MaxRule.class, "[max number]");
        this.ruleCommands.put(SpawnEnvironmentRule.class, "[NORMAL,NETHER]");
        this.ruleCommands.put(SpawnHeightRule.class, "[max] [min]");
        this.ruleCommands.put(SpawnLightRule.class, "[max] [min]");
        this.ruleCommands.put(SpawnMaterialRule.class, "[material name list] [spawnable]");
        this.ruleCommands.put(TargetPlayerRule.class, "[player] [targetable(true,false)]");
        this.ruleCommands.put(SpawnReplaceRule.class, "[creature name]");
        this.ruleCommands.put(SpawnLocationRule.class, "[elRegion name]");
        this.ruleCommands.put(MovementLocationRule.class, "[elRegion name]");
        this.ruleCommands.put(SpawnTimeRule.class, "[Day or Night]");
        
        if (!this.getDataFolder().exists()) {
            if (this.getDataFolder().mkdirs()) {
                // Create dir if it doesn't exist
                this.log.info("[CrowdControl] Made data folder!");
            }
            FileUtils.copyResourcesRecursively(super.getClass().getResource("/config"), this.getDataFolder());
        }
        
        File configFile = new File(this.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
        
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                this.log.info("Unable to make config.yml!");
            }
        }
        
        this.config = new Configuration(configFile);
        
        // Register our events
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvent(Type.CREATURE_SPAWN, this.entityListener, Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_TARGET, this.entityListener, Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_COMBUST, this.entityListener, Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_EXPLODE, this.entityListener, Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_DAMAGE, this.entityListener, Priority.Lowest, this);
        pm.registerEvent(Type.CHUNK_UNLOAD, this.worldListener, Priority.Monitor, this);
        
        // Register command
        this.getCommand("crowd").setExecutor(new CrowdCommand(this));
        
        this.loadConfigFile();
        
        this.log.info(this.pdf.getFullName() + " is enabled!");
    }
    
    /**
     * Registers a listener
     * 
     * @param listener
     *            {@link CrowdListener}
     */
    @ThreadSafe
    public void registerListener(CrowdListener listener) {
        this.listeners.add(listener);
    }
    
    /**
     * Sets the despawn distance
     * 
     * @param despawnDistance
     *            {@link Integer}
     */
    public void setDespawnDistance(int despawnDistance) {
        this.despawnDistance = despawnDistance;
        
        this.config.setProperty("global.despawnDistance", despawnDistance);
        this.config.save();
    }
    
    /**
     * Sets the idle despawn chance
     * 
     * @param idleDespawnChance
     *            {@link Double}
     */
    public void setIdleDespawnChance(double idleDespawnChance) {
        this.idleDespawnChance = idleDespawnChance;
        
        this.config.setProperty("global.idleDespawnChance", idleDespawnChance);
        this.config.save();
    }
    
    /**
     * Sets the max per chunk
     * 
     * @param max
     *            {@link Integer}
     */
    public void setMaxPerChunk(int max) {
        this.maxPerChunk = max;
        
        this.config.setProperty("global.maxPerChunk", max);
        this.config.save();
    }
    
    /**
     * Sets the max per world
     * 
     * @param max
     *            {@link Integer}
     */
    public void setMaxPerWorld(int max) {
        this.maxPerWorld = max;
        
        this.config.setProperty("global.maxPerWorld", max);
        this.config.save();
    }
    
    /**
     * Sets how close a crowd creature can spawn to a player
     * 
     * @param minDistanceFromPlayer
     *            {@link Integer}
     */
    public void setMinDistanceFromPlayer(int minDistanceFromPlayer) {
        this.minDistanceFromPlayer = minDistanceFromPlayer;
        
        this.config.setProperty("global.minDistanceFromPlayer", minDistanceFromPlayer);
        this.config.save();
    }
    
    /**
     * Sets if a slime should split
     * 
     * @param split
     *            true if you want them to split to smaller slimes
     */
    public void setSlimeSplit(boolean split) {
        this.slimeSplit = split;
        
        this.config.setProperty("global.slimeSplit", split);
        this.config.save();
    }
    
    /**
     * Sets the chance of a skeleton rides a spider
     * 
     * @param spiderRiderChance
     *            the chance 0.0 - 1.0
     */
    public void setSpiderRiderChance(double spiderRiderChance) {
        this.spiderRiderChance = spiderRiderChance;
        
        this.config.setProperty("global.spiderRiderChance", spiderRiderChance);
        this.config.save();
    }
}
