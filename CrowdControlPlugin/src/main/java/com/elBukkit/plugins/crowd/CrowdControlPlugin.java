package com.elBukkit.plugins.crowd;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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

import com.alta189.sqlLibraryV2.SQLite.sqlCore;
import com.elBukkit.plugins.crowd.creature.BaseInfo;
import com.elBukkit.plugins.crowd.creature.CreatureHandler;
import com.elBukkit.plugins.crowd.creature.CrowdCreature;
import com.elBukkit.plugins.crowd.events.CrowdListener;
import com.elBukkit.plugins.crowd.rules.MaxRule;
import com.elBukkit.plugins.crowd.rules.Rule;
import com.elBukkit.plugins.crowd.rules.SpawnEnvironmentRule;
import com.elBukkit.plugins.crowd.rules.SpawnHeightRule;
import com.elBukkit.plugins.crowd.rules.SpawnLightRule;
import com.elBukkit.plugins.crowd.rules.SpawnLocationRule;
import com.elBukkit.plugins.crowd.rules.SpawnMaterialRule;
import com.elBukkit.plugins.crowd.rules.SpawnReplaceRule;
import com.elBukkit.plugins.crowd.rules.SpawnTimeRule;
import com.elBukkit.plugins.crowd.rules.TargetPlayerRule;

/*
 * CrowdControl plugin
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdControlPlugin extends JavaPlugin {

	private static Lock cHandlerLock = new ReentrantLock();
	private Configuration config;
	public ConcurrentHashMap<World, CreatureHandler> creatureHandlers = new ConcurrentHashMap<World, CreatureHandler>();
	public sqlCore dbManage; // import SQLite lib

	private CrowdEntityListener entityListener = new CrowdEntityListener(this);
	private Set<CrowdListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<CrowdListener, Boolean>());
	private Logger log;
	
	private volatile int maxPerChunk = 2;
	private volatile int maxPerWorld = 300;
	private volatile int despawnDistance = 128;
	private volatile double idleDespawnChance = 0.05;
	private volatile int minDistanceFromPlayer = 10;

	private PluginDescriptionFile pdf;

	private ConcurrentHashMap<Class<? extends Rule>, String> ruleCommands;
	public RuleHandler ruleHandler;
	private CrowdWorldListener worldListener = new CrowdWorldListener(this);

	@ThreadSafe
	public CreatureHandler getCreatureHandler(World w) {
		if (creatureHandlers.containsKey(w)) {
			return creatureHandlers.get(w);
		} else {
			CreatureHandler creatureHandler;
			try {
				if (cHandlerLock.tryLock()) {
					creatureHandler = new CreatureHandler(dbManage, w, this);
					// Register the despawner
					getServer().getScheduler().scheduleSyncRepeatingTask(this, creatureHandler, 0, 10);
					creatureHandlers.put(w, creatureHandler);
					return creatureHandler;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				cHandlerLock.unlock();
			}
		}
		return null;
	}

	@ThreadSafe
	public Set<CrowdListener> getListeners() {
		return this.listeners;
	}

	@ThreadSafe
	public int getMaxPerChunk() {
		return this.maxPerChunk;
	}

	@ThreadSafe
	public int getMaxPerWorld() {
		return this.maxPerWorld;
	}

	@ThreadSafe
	public Map<Class<? extends Rule>, String> getRules() {
		return ruleCommands;
	}

	public void onDisable() {
		log.info(pdf.getFullName() + " is disabled!");
	}

	public void onEnable() {
		pdf = this.getDescription();
		log = this.getServer().getLogger();
		log.info(pdf.getFullName() + " is enabled!");

		ruleCommands = new ConcurrentHashMap<Class<? extends Rule>, String>();
		ruleCommands.put(MaxRule.class, "[max number]");
		ruleCommands.put(SpawnEnvironmentRule.class, "[NORMAL,NETHER]");
		ruleCommands.put(SpawnHeightRule.class, "[max,min]");
		ruleCommands.put(SpawnLightRule.class, "[max,min]");
		ruleCommands.put(SpawnMaterialRule.class, "[material name]");
		ruleCommands.put(TargetPlayerRule.class, "[player,targetable(true,false)]");
		ruleCommands.put(SpawnReplaceRule.class, "[creature name]");
		ruleCommands.put(SpawnLocationRule.class, "[x1,y1,z1,x2,y2,z2]");
		ruleCommands.put(SpawnTimeRule.class, "[Day or Night]");

		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdirs(); // Create dir if it doesn't exist
		}

		String prefix = "[CrowdControl]";
		String dbName = pdf.getName();

		dbManage = new sqlCore(this.getServer().getLogger(), prefix, dbName, this.getDataFolder().getAbsolutePath());
		try {
			ruleHandler = new RuleHandler(dbManage, this);
		} catch (Exception e) {
			e.printStackTrace();
			this.setEnabled(false);
			return;
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
		
		loadConfigFile();

		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_COMBUST, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.CHUNK_UNLOAD, worldListener, Priority.Monitor, this);

		// Register command
		getCommand("crowd").setExecutor(new CrowdCommand(this));

		for (World w : Bukkit.getServer().getWorlds()) {

			CreatureHandler cHandler = getCreatureHandler(w); // Create all of
																// the creature
																// handlers

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

	@ThreadSafe
	public void registerListener(CrowdListener listener) {
		this.listeners.add(listener);
	}

	public void setMaxPerChunk(int max) {
		this.maxPerChunk = max;

		config.setProperty("global.maxPerChunk", max);
	}

	public void setMaxPerWorld(int max) {
		this.maxPerWorld = max;

		config.setProperty("global.maxPerWorld", max);
	}

	public void setDespawnDistance(int despawnDistance) {
		this.despawnDistance = despawnDistance;

		config.setProperty("global.despawnDistance", despawnDistance);
	}

	public int getDespawnDistance() {
		return despawnDistance;
	}

	public void setIdleDespawnChance(double idleDespawnChance) {
		this.idleDespawnChance = idleDespawnChance;

		config.setProperty("global.idleDespawnChance", idleDespawnChance);
	}

	public double getIdleDespawnChance() {
		return idleDespawnChance;
	}

	public void setMinDistanceFromPlayer(int minDistanceFromPlayer) {
		this.minDistanceFromPlayer = minDistanceFromPlayer;

		config.setProperty("global.minDistanceFromPlayer", minDistanceFromPlayer);
	}

	public int getMinDistanceFromPlayer() {
		return minDistanceFromPlayer;
	}
	
	public void loadConfigFile() {
		if (config.getNode("global") != null) {
			this.despawnDistance = config.getInt("global.despawnDistance", this.despawnDistance);
			this.idleDespawnChance = config.getDouble("global.idleDespawnChance", this.idleDespawnChance);
			this.maxPerChunk = config.getInt("global.maxPerChunk", this.maxPerChunk);
			this.maxPerWorld = config.getInt("global.maxPerWorld", this.maxPerWorld);
			this.minDistanceFromPlayer = config.getInt("global.minDistanceFromPlayer", this.minDistanceFromPlayer);
		} else {
			config.setProperty("global.despawnDistance", this.despawnDistance);
			config.setProperty("global.idleDespawnChance", this.idleDespawnChance);
			config.setProperty("global.maxPerChunk", this.maxPerChunk);
			config.setProperty("global.maxPerWorld", this.maxPerWorld);
			config.setProperty("global.minDistanceFromPlayer", this.minDistanceFromPlayer);
		}
	}
}
