package com.elBukkit.plugins.crowd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
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
import com.elBukkit.plugins.crowd.rules.TargetPlayerRule;

/*
 * CrowdControl plugin
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdControlPlugin extends JavaPlugin {

	private static Lock cHandlerLock = new ReentrantLock();
	private File configFile;
	public ConcurrentHashMap<World, CreatureHandler> creatureHandlers = new ConcurrentHashMap<World, CreatureHandler>();
	public sqlCore dbManage; // import SQLite lib

	private CrowdEntityListener entityListener = new CrowdEntityListener(this);
	private Set<CrowdListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<CrowdListener, Boolean>());
	private Logger log;

	private volatile int maxPerChunk = 4;

	private volatile int maxPerWorld = 200;

	private PluginDescriptionFile pdf;
	private ConcurrentHashMap<Class<? extends Rule>, String> ruleCommands;
	public RuleHandler ruleHandler;

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

		configFile = new File(this.getDataFolder().getAbsolutePath() + File.separator + "config.txt");

		try {
			if (!configFile.exists()) {
				configFile.createNewFile();

				PrintWriter globalConfigWriter = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));

				globalConfigWriter.println("maxPerWorld:" + String.valueOf(this.maxPerWorld));
				globalConfigWriter.println("maxPerChunk:" + String.valueOf(this.maxPerChunk));

				globalConfigWriter.close();
			}

			Scanner globalConfigReader = new Scanner(new FileInputStream(configFile));
			globalConfigReader.useDelimiter(System.getProperty("line.separator"));

			while (globalConfigReader.hasNext()) {
				String[] data = processLine(globalConfigReader.next());

				if (data != null) {
					if (data[0].equalsIgnoreCase("maxPerWorld")) {
						this.maxPerWorld = Integer.parseInt(data[1]);
					} else if (data[0].equalsIgnoreCase("maxPerChunk")) {
						this.maxPerChunk = Integer.parseInt(data[1]);
					}
				}
			}

			globalConfigReader.reset();

		} catch (IOException e) {
			log.info("Failed to read config file!");
			this.setEnabled(false);
		}

		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_COMBUST, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);

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

	private String[] processLine(String aLine) {
		Scanner scanner = new Scanner(aLine);
		scanner.useDelimiter(":");
		if (scanner.hasNext()) {
			String[] returnData = { "", "" };
			returnData[0] = scanner.next();
			returnData[1] = scanner.next();
			return returnData;
		} else {
			log.info("Empty or invalid line. Unable to process.");
			return null;
		}
	}

	@ThreadSafe
	public void registerListener(CrowdListener listener) {
		this.listeners.add(listener);
	}

	public void setMaxPerChunk(int max) throws IOException {
		PrintWriter globalConfigWriter = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));

		this.maxPerChunk = max;

		this.configFile.delete();
		this.configFile.createNewFile();

		globalConfigWriter.println("maxPerWorld:" + String.valueOf(this.maxPerWorld));
		globalConfigWriter.println("maxPerChunk:" + String.valueOf(this.maxPerChunk));

		globalConfigWriter.close();
	}

	public void setMaxPerWorld(int max) throws IOException {

		PrintWriter globalConfigWriter = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));

		this.maxPerWorld = max;

		this.configFile.delete();
		this.configFile.createNewFile();

		globalConfigWriter.println("maxPerWorld:" + String.valueOf(this.maxPerWorld));
		globalConfigWriter.println("maxPerChunk:" + String.valueOf(this.maxPerChunk));

		globalConfigWriter.close();
	}
}
