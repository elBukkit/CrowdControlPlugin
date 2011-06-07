package com.elBukkit.bukkit.plugins.crowd;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Creature;
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

	private Location location;
	private Environment env;
	private CreatureType type;
	private LivingEntity entity;
	private Creature creature;
	private TargetReason reason;
	private Entity target;

	public void setTarget(Entity target) {
		this.target = target;
	}

	public Entity getTarget() {
		return target;
	}

	public void setCreature(Creature creature) {
		this.creature = creature;
	}

	public Creature getCreature() {
		return creature;
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

	}

	public LivingEntity getEntity() {
		return entity;
	}

}