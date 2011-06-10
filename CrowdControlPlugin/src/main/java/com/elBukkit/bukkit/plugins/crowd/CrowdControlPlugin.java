package com.elBukkit.bukkit.plugins.crowd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.elBukkit.bukkit.plugins.crowd.creature.CreatureHandler;
import com.elBukkit.bukkit.plugins.crowd.creature.DamageHandler;
import com.elBukkit.bukkit.plugins.crowd.rules.MaxRule;
import com.elBukkit.bukkit.plugins.crowd.rules.Rule;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnEnvironmentRule;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnHeightRule;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnLightRule;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnMaterialRule;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnReplaceRule;
import com.elBukkit.bukkit.plugins.crowd.rules.TargetPlayerRule;

/*
 * CrowdControl plugin
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdControlPlugin extends JavaPlugin {

	private CrowdEntityListener entityListener = new CrowdEntityListener(this);
	private PluginDescriptionFile pdf;
	public Map<Class<? extends Rule>, String> ruleCommands;

	public RuleHandler ruleHandler;
	public CreatureHandler creatureHandler;

	public sqlCore dbManage; // import SQLite lib

	public void onDisable() {
		System.out.println(pdf.getFullName() + " is disabled!");

	}

	public void onEnable() {
		pdf = this.getDescription();
		System.out.println(pdf.getFullName() + " is enabled!");

		ruleCommands = new HashMap<Class<? extends Rule>, String>();
		ruleCommands.put(MaxRule.class, "[max number]");
		ruleCommands.put(SpawnEnvironmentRule.class, "[NORMAL,NETHER]");
		ruleCommands.put(SpawnHeightRule.class, "[max,min]");
		ruleCommands.put(SpawnLightRule.class, "[max,min]");
		ruleCommands.put(SpawnMaterialRule.class, "[material name]");
		ruleCommands.put(TargetPlayerRule.class,
				"[player,targetable(true,false)]");
		ruleCommands.put(SpawnReplaceRule.class, "[creature name]");

		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs(); // Create dir if it doesn't exist

		String prefix = "[CrowdControl]";
		String dbName = pdf.getName() + ".db";

		dbManage = new sqlCore(this.getServer().getLogger(), prefix, dbName,
				this.getDataFolder().getAbsolutePath());
		try {
			ruleHandler = new RuleHandler(dbManage);
			creatureHandler = new CreatureHandler(dbManage);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			this.setEnabled(false);
			return;
		}

		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest,
				this);
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Highest,
				this);
		pm.registerEvent(Type.ENTITY_COMBUST, entityListener, Priority.Highest,
				this);
		pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest,
				this);

		// Register command
		getCommand("crowd").setExecutor(new CrowdCommand(this));

		// Register the creature handler repeating task
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				creatureHandler, 0, 5); // Start immediately and call every 5
										// ticks

		// Register the damage handler
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new DamageHandler(this), 0, 20); // Start immediately and call
													// every 20
		// ticks(1 second)
	}
}
