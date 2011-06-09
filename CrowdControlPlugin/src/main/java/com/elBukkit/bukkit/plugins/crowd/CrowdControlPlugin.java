package com.elBukkit.bukkit.plugins.crowd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.WaterMob;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.elBukkit.bukkit.plugins.crowd.creature.CreatureHandler;
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

		// Register command
		getCommand("crowd").setExecutor(new CrowdCommand(this));
	}

	public CreatureType getCreatureType(Entity entity) {
		if (entity instanceof LivingEntity) {
			if (entity instanceof Creature) {
				// Animals
				if (entity instanceof Animals) {
					if (entity instanceof Chicken) {
						return CreatureType.CHICKEN;
					} else if (entity instanceof Cow) {
						return CreatureType.COW;
					} else if (entity instanceof Pig) {
						return CreatureType.PIG;
					} else if (entity instanceof Sheep) {
						return CreatureType.SHEEP;
					}
				}
				// Monsters
				else if (entity instanceof Monster) {
					if (entity instanceof Zombie) {
						if (entity instanceof PigZombie) {
							return CreatureType.PIG_ZOMBIE;
						}
					} else if (entity instanceof Creeper) {
						return CreatureType.CREEPER;
					} else if (entity instanceof Giant) {
						return CreatureType.GIANT;
					} else if (entity instanceof Skeleton) {
						return CreatureType.SKELETON;
					} else if (entity instanceof Spider) {
						return CreatureType.SPIDER;
					} else if (entity instanceof Slime) {
						return CreatureType.SLIME;
					}
				}
				// Water Animals
				else if (entity instanceof WaterMob) {
					if (entity instanceof Squid) {
						return CreatureType.SQUID;
					}
				}
			}
			// Flying
			else if (entity instanceof Flying) {
				if (entity instanceof Ghast) {
					return CreatureType.GHAST;
				}
			}
		}
		return null;
	}
}
