package com.elBukkit.bukkit.plugins.crowd;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

/*
 * Info about creature spawning
 * 
 * @author Andrew Querol(WinSock)
 */

public class Info {

	private LivingEntity entity;
	private Environment env;
	private int id;
	private Location location;
	private TargetReason reason;
	private Entity target;
	private CreatureType type;

	public LivingEntity getEntity() {
		return entity;
	}

	public Environment getEnv() {
		return env;
	}

	public int getID() {
		return this.id;
	}

	public Location getLocation() {
		return location;
	}

	public TargetReason getReason() {
		return reason;
	}

	public Entity getTarget() {
		return target;
	}

	public CreatureType getType() {
		return type;
	}

	public void setEntity(LivingEntity entity) {
		this.entity = entity;
	}

	public void setEnv(Environment env) {
		this.env = env;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setReason(TargetReason reason) {
		this.reason = reason;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	public void setType(CreatureType type) {
		this.type = type;
	}

	public void spawn() {
		this.entity = location.getWorld().spawnCreature(location, type);
	}
}
