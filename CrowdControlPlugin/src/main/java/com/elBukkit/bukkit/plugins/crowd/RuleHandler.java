package com.elBukkit.bukkit.plugins.crowd;

import java.util.HashSet;
import java.util.Set;

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

	private Set<SpawnRule> spawnRules;
	private Set<TargetRule> targetRules;
	
	private sqlCore dbManage;

	// TODO Add when entity movement events are added, feature request #157
	// private Set<SpawnRule> movmentRules;

	public RuleHandler(sqlCore dbManage) {
		spawnRules = new HashSet<SpawnRule>();
		this.dbManage = dbManage;
		
		dbManage.initialize();
		if (!dbManage.checkTable("Rules"))
		{
			String createDB = "CREATE TABLE spawnRules" +
			"(" +
			"Id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
			"Rule VARCHAR(255), " +
			"Worlds VARCHAR(255), " +
			"Creatures VARCHAR(255), " +
			"Data VARCHAR(255)" +
			");";
			dbManage.createTable(createDB);
		}
		dbManage.close();
	}

	public void AddRule(SpawnRule rule) {
		this.spawnRules.add(rule);

		// Do persistence
	}

	public boolean RemoveRule(SpawnRule rule) {
		if (spawnRules.contains(rule)) {
			spawnRules.remove(rule);
			return true;

			// Remove rule from persistence
		}
		return false;
	}

	public void AddRule(TargetRule rule) {
		this.targetRules.add(rule);

		// Do persistence
	}

	public boolean RemoveRule(TargetRule rule) {
		if (targetRules.contains(rule)) {
			targetRules.remove(rule);
			return true;

			// Remove rule from persistence
		}
		return false;
	}

	public boolean passesRules(SpawnInfo info) {
		for (SpawnRule r : spawnRules) {
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
		for (TargetRule r : targetRules) {
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
