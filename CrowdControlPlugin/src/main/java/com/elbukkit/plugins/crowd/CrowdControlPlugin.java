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

    private static Lock cHandlerLock = new ReentrantLock();
    private static Lock rHandlerLock = new ReentrantLock();
    private Configuration config;
    private ConcurrentHashMap<World, CreatureHandler> creatureHandlers;
    private volatile int despawnDistance = 128;

    private elRegionsPlugin elRegions;
    private CrowdEntityListener entityListener = new CrowdEntityListener(this);
    private volatile double idleDespawnChance = 0.05;

    private Set<CrowdListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<CrowdListener, Boolean>());

    private Logger log;
    private volatile int maxPerChunk = 2;
    private volatile int maxPerWorld = 300;
    private volatile int minDistanceFromPlayer = 10;
    private PluginDescriptionFile pdf;
    private ConcurrentHashMap<Class<? extends Rule>, String> ruleCommands;
    private ConcurrentHashMap<World, RuleHandler> ruleHandlers;

    private volatile boolean slimeSplit = true;

    private volatile double spiderRiderChance = 0.01;
    private CrowdWorldListener worldListener = new CrowdWorldListener(this);

    /**
     * Gets a creature handler for a {@link World}
     * 
     * @param w
     *            {@link World}
     * @return {@link CreatureHandler}
     */
    @ThreadSafe
    public CreatureHandler getCreatureHandler(World w) {
        if (creatureHandlers.containsKey(w)) {
            return creatureHandlers.get(w);
        } else {
            CreatureHandler creatureHandler = null;
            if (cHandlerLock.tryLock()) {
                try {
                    creatureHandler = new CreatureHandler(w, this);
                    // Register the despawner
                    getServer().getScheduler().scheduleSyncRepeatingTask(this, creatureHandler, 0, 10);
                    creatureHandlers.put(w, creatureHandler);
                    return creatureHandler;
                } catch (IOException e) {
                    log.info("[CrowdControl] Error making creature handler!");
                } finally {
                    cHandlerLock.unlock();
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
        return despawnDistance;
    }

    /**
     * Returns the despawn chance when idle
     * 
     * @return A {@link Double} representing the chance
     */
    @ThreadSafe
    public double getIdleDespawnChance() {
        return idleDespawnChance;
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
        return log;
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
        return minDistanceFromPlayer;
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
        if (ruleHandlers.containsKey(w)) {
            return ruleHandlers.get(w);
        } else {
            RuleHandler ruleHandler = null;
            if (rHandlerLock.tryLock()) {
                try {
                    ruleHandler = new RuleHandler(w, this);
                    ruleHandlers.put(w, ruleHandler);
                    return ruleHandler;
                } catch (ClassNotFoundException e) {
                    log.info("Error loading rules!");
                } catch (NoSuchMethodException e) {
                    log.info("Error loading rules!");
                } catch (InstantiationException e) {
                    log.info("Error loading rules!");
                } catch (IllegalAccessException e) {
                    log.info("Error loading rules!");
                } catch (InvocationTargetException e) {
                    log.info("Error loading rules!");
                } catch (IOException e) {
                    log.info("Error loading rules!");
                } finally {
                    rHandlerLock.unlock();
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
        return ruleCommands;
    }

    /**
     * Gets if a slime should split.
     * 
     * @return If a slime should split.
     */

    public boolean getSlimeSplit() {
        return slimeSplit;
    }

    /**
     * Gets the chance of a spider spawning with a skeleton riding it
     * 
     * @return the chance 0.0 - 1.0
     */
    public double getSpiderRiderChance() {
        return spiderRiderChance;
    }

    /**
     * Loads or reloads the config file
     */
    public void loadConfigFile() {
        config.load();
        if (config.getNode("global") != null) {
            this.despawnDistance = config.getInt("global.despawnDistance", this.despawnDistance);
            this.idleDespawnChance = config.getDouble("global.idleDespawnChance", this.idleDespawnChance);
            this.maxPerChunk = config.getInt("global.maxPerChunk", this.maxPerChunk);
            this.maxPerWorld = config.getInt("global.maxPerWorld", this.maxPerWorld);
            this.minDistanceFromPlayer = config.getInt("global.minDistanceFromPlayer", this.minDistanceFromPlayer);
            this.slimeSplit = config.getBoolean("global.slimeSplit", this.slimeSplit);
            this.spiderRiderChance = config.getDouble("global.spiderRiderChance", this.spiderRiderChance);
        } else {
            config.setProperty("global.despawnDistance", this.despawnDistance);
            config.setProperty("global.idleDespawnChance", this.idleDespawnChance);
            config.setProperty("global.maxPerChunk", this.maxPerChunk);
            config.setProperty("global.maxPerWorld", this.maxPerWorld);
            config.setProperty("global.minDistanceFromPlayer", this.minDistanceFromPlayer);
            config.setProperty("global.slimeSplit", this.slimeSplit);
            config.setProperty("global.spiderRiderChance", this.spiderRiderChance);
        }
        config.save();

        ruleHandlers = new ConcurrentHashMap<World, RuleHandler>();
        creatureHandlers = new ConcurrentHashMap<World, CreatureHandler>();
        this.getServer().getScheduler().cancelTasks(this);

        for (World w : Bukkit.getServer().getWorlds()) {

            CreatureHandler cHandler = getCreatureHandler(w); // Create all of
                                                              // the creature
                                                              // handlers
            getRuleHandler(w); // Create the rule handlers

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
        log.info(pdf.getFullName() + " is disabled!");
    }

    public void onEnable() {
        pdf = this.getDescription();
        log = this.getServer().getLogger();

        elRegions = (elRegionsPlugin) this.getServer().getPluginManager().getPlugin("elRegions");

        if (elRegions == null) {
            log.info("ERROR: Could not load elRegions!");
            this.setEnabled(false);
            return;
        }

        ruleCommands = new ConcurrentHashMap<Class<? extends Rule>, String>();
        ruleCommands.put(MaxRule.class, "[max number]");
        ruleCommands.put(SpawnEnvironmentRule.class, "[NORMAL,NETHER]");
        ruleCommands.put(SpawnHeightRule.class, "[max,min]");
        ruleCommands.put(SpawnLightRule.class, "[max,min]");
        ruleCommands.put(SpawnMaterialRule.class, "[material name list] [spawnable]");
        ruleCommands.put(TargetPlayerRule.class, "[player,targetable(true,false)]");
        ruleCommands.put(SpawnReplaceRule.class, "[creature name]");
        ruleCommands.put(SpawnLocationRule.class, "[elRegion name]");
        ruleCommands.put(MovementLocationRule.class, "[elRegion name]");
        ruleCommands.put(SpawnTimeRule.class, "[Day or Night]");

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs(); // Create dir if it doesn't exist
            FileUtils.copyResourcesRecursively(super.getClass().getResource("/config"), getDataFolder());
        }

        File configFile = new File(this.getDataFolder().getAbsolutePath() + File.separator + "config.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                log.info("Unable to make config.yml!");
            }
        }

        config = new Configuration(configFile);

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_COMBUST, entityListener, Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Lowest, this);
        pm.registerEvent(Type.CHUNK_UNLOAD, worldListener, Priority.Monitor, this);

        // Register command
        getCommand("crowd").setExecutor(new CrowdCommand(this));

        loadConfigFile();

        log.info(pdf.getFullName() + " is enabled!");
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

        config.setProperty("global.despawnDistance", despawnDistance);
        config.save();
    }

    /**
     * Sets the idle despawn chance
     * 
     * @param idleDespawnChance
     *            {@link Double}
     */
    public void setIdleDespawnChance(double idleDespawnChance) {
        this.idleDespawnChance = idleDespawnChance;

        config.setProperty("global.idleDespawnChance", idleDespawnChance);
        config.save();
    }

    /**
     * Sets the max per chunk
     * 
     * @param max
     *            {@link Integer}
     */
    public void setMaxPerChunk(int max) {
        this.maxPerChunk = max;

        config.setProperty("global.maxPerChunk", max);
        config.save();
    }

    /**
     * Sets the max per world
     * 
     * @param max
     *            {@link Integer}
     */
    public void setMaxPerWorld(int max) {
        this.maxPerWorld = max;

        config.setProperty("global.maxPerWorld", max);
        config.save();
    }

    /**
     * Sets how close a crowd creature can spawn to a player
     * 
     * @param minDistanceFromPlayer
     *            {@link Integer}
     */
    public void setMinDistanceFromPlayer(int minDistanceFromPlayer) {
        this.minDistanceFromPlayer = minDistanceFromPlayer;

        config.setProperty("global.minDistanceFromPlayer", minDistanceFromPlayer);
        config.save();
    }

    /**
     * Sets if a slime should split
     * 
     * @param split
     *            true if you want them to split to smaller slimes
     */
    public void setSlimeSplit(boolean split) {
        this.slimeSplit = split;

        config.setProperty("global.slimeSplit", split);
        config.save();
    }

    /**
     * Sets the chance of a skeleton rides a spider
     * 
     * @param spiderRiderChance
     *            the chance 0.0 - 1.0
     */
    public void setSpiderRiderChance(double spiderRiderChance) {
        this.spiderRiderChance = spiderRiderChance;

        config.setProperty("global.spiderRiderChance", spiderRiderChance);
        config.save();
    }
}
