package com.elBukkit.bukkit.plugins.crowd.rules;

import org.bukkit.World;
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

import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * Rule class, basic methods required for checking
 * 
 * @author Andrew Querol(WinSock)
 * 
 */

public class Rule {

	protected World world;
	protected CreatureType type;
	protected Type ruleType;

	public Rule(World world, CreatureType type) {
		this.world = world;
		this.type = type;
	}

	public boolean check(Info info) {
		return true;
	} // Check if creature passes

	public boolean checkWorld(World world) { // Check if the world is effected
												// by this rule
		return this.world.equals(world);
	}

	public boolean checkCreatureType(CreatureType type) { // Check if the
															// creature is
															// effected by the
															// rule
		return this.type.equals(type);
	}

	// Classes used for saving data
	public CreatureType getCreatureType() {
		return type;
	}

	public World getWorld() {
		return world;
	}

	public Type getType() {
		return ruleType;
	}

	public String getData() {
		return "";
	}

	public void init(String data) {
	}

	protected CreatureType getCreatureType(Entity entity) {
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
