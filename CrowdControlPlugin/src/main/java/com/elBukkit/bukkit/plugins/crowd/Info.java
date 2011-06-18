package com.elBukkit.bukkit.plugins.crowd;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
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
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

/*
 * Info about creature spawning
 * 
 * @author Andrew Querol(WinSock)
 */

public class Info {

	private Location location;
	private Environment env;
	private CreatureType type;
	private LivingEntity entity;
	private TargetReason reason;
	private Entity target;
	private int id;

	public void setTarget(Entity target) {
		this.target = target;
	}

	public Entity getTarget() {
		return target;
	}

	public void setReason(TargetReason reason) {
		this.reason = reason;
	}

	public TargetReason getReason() {
		return reason;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public void setEnv(Environment env) {
		this.env = env;
	}

	public Environment getEnv() {
		return env;
	}

	public void setType(CreatureType type) {
		this.type = type;
	}

	public CreatureType getType() {
		return type;
	}

	public void spawn() {
		Entity e = location.getWorld().spawn(location, getClassFromType(type));
		if (e instanceof LivingEntity) {
			entity = (LivingEntity) e;
		}
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public void setEntity(LivingEntity entity) {
		this.entity = entity;
	}

	public int getID() {
		return this.id;
	}

	private Class<? extends Entity> getClassFromType(CreatureType type) {
		switch (type) {
		case CHICKEN:
			return Chicken.class;
		case COW:
			return Cow.class;
		case CREEPER:
			return Creeper.class;
		case GHAST:
			return Ghast.class;
		case GIANT:
			return Giant.class;
		case MONSTER:
			return Monster.class;
		case PIG:
			return Pig.class;
		case PIG_ZOMBIE:
			return PigZombie.class;
		case SHEEP:
			return Sheep.class;
		case SKELETON:
			return Skeleton.class;
		case SLIME:
			return Slime.class;
		case SPIDER:
			return Spider.class;
		case SQUID:
			return Squid.class;
		case ZOMBIE:
			return Zombie.class;
		case WOLF:
			return Wolf.class;
		default:
			return Creature.class;
		}
	}
}
