package com.elBukkit.bukkit.plugins.crowd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
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

import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnRule;
import com.elBukkit.bukkit.plugins.crowd.rules.TargetRule;

/*
 * Handles all of the rules and checks if the triggering creatures passes
 * 
 * @author Andrew Querol(WinSock)
 */

public class RuleHandler {

	private Map<SpawnRule, Integer> spawnRules;
	private Map<TargetRule, Integer> targetRules;
	
	private sqlCore dbManage;

	// TODO Add when entity movement events are added, feature request #157
	// private Set<SpawnRule> movmentRules;

	public RuleHandler(sqlCore dbManage) throws SQLException, ClassNotFoundException {
		spawnRules = new HashMap<SpawnRule, Integer>();
		targetRules = new HashMap<TargetRule, Integer>();
		
		this.dbManage = dbManage;
		
		dbManage.initialize();
		if (!dbManage.checkTable("Rules"))
		{
			String createDB = "CREATE TABLE spawnRules" +
				"(" +
				"Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"Rule VARCHAR(255), " +
				"Worlds VARCHAR(255), " +
				"Creatures VARCHAR(255), " +
				"Data VARCHAR(255)" +
				");";
			dbManage.createTable(createDB);
		}
		else
		{
			String selectSQL = "SELECT * FROM spawnRules;";
			ResultSet rs = dbManage.sqlQuery(selectSQL);
			
			while(rs.next())
			{
				String ruleClass, worlds, creatures, data;
				ruleClass = rs.getString(2);
				worlds = rs.getString(3);
				creatures = rs.getString(4);
				data = rs.getString(5);
				
				Class rule = Class.forName(ruleClass);
				
				// TODO Figure out how to use reflection to get it back to the right rule
			}
			
		}
		dbManage.close();
	}

	public void AddRule(SpawnRule rule) throws SQLException {

		String worlds = "";
		
		for(World w : rule.getWorlds())
		{
			worlds += w.getName();
		}
		
		String addRuleSQL = "INSERT INTO spawnRules (Rule,Worlds,Creatures,Data) " +
				"VALUES(" + rule.getClass().getName() + 
				", " + worlds +
				", " + rule.getCreatureType().toString() +
				", " + rule.getData() + 
				");";
		
		dbManage.initialize();
		dbManage.insertQuery(addRuleSQL);
		spawnRules.put(rule, dbManage.sqlQuery("SELECT last_insert_rowid();").getInt(0));
		dbManage.close();
	}

	public void RemoveRule(SpawnRule rule) {
		String removeSQL = "DELETE * FROM spawnRules WHERE " +
			"Id = '" + String.valueOf(targetRules.get(rule)) +
			"';";
		dbManage.deleteQuery(removeSQL);
		dbManage.close();
		spawnRules.remove(rule);
	}

	public void AddRule(TargetRule rule) throws SQLException {

		String worlds = "";
		
		for(World w : rule.getWorlds())
		{
			worlds += w.getName();
		}
		
		String addRuleSQL = "INSERT INTO spawnRules (Rule,Worlds,Creatures,Data) " +
		"VALUES(" + rule.getClass().getName() + 
		", " + worlds +
		", " + rule.getCreatureType().toString() +
		", " + rule.getData() + 
		");";

		dbManage.initialize();
		dbManage.insertQuery(addRuleSQL);
		targetRules.put(rule, dbManage.sqlQuery("SELECT last_insert_rowid();").getInt(0));
		dbManage.close();
		
	}

	public void RemoveRule(TargetRule rule) {
		String removeSQL = "DELETE * FROM spawnRules WHERE " +
				"Id = '" + String.valueOf(targetRules.get(rule)) +
				"';";
		dbManage.deleteQuery(removeSQL);
		dbManage.close();
		targetRules.remove(rule);
	}

	public boolean passesRules(SpawnInfo info) {
		for (SpawnRule r : spawnRules.keySet()) {
			if (r.checkWorld(info.getLocation().getWorld())) {
				if (r.checkCreatureType(info.getType())) {
					if (r.spawn(info)) {
						return true;
					}
					return false;
				}
			}
		}
		return true;
	}

	public boolean passesRules(TargetInfo info) {
		for (TargetRule r : targetRules.keySet()) {
			if (r.checkWorld(info.getCreature().getLocation().getWorld())) {
				if (r.checkCreatureType(getCreatureType((Entity) info
						.getCreature()))) {
					if (r.target(info)) {
						return true;
					}
					return false;
				}
			}
		}
		return true;
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
