package com.elmakers.mine.bukkit.plugins.crowd;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;

public class SpawnInfo {

	private Location location;
	private Environment env;
	private CreatureType type;
	private LivingEntity entity;
	
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
	
	public void spawn()
	{
		entity = location.getWorld().spawnCreature(location, type);
	}
	
	public LivingEntity getEntity() {
		return entity;
	}
	
}
