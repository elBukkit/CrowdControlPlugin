package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.SpawnInfo;

/*
 * A rule that allows or disallows creatures based on the environment, Normal, Nether, (Soon) Ather
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnEnvironmentRule implements SpawnRule {

	private Set<Environment> spawnableEnvironment;
	private Set<World> worlds;
	private CreatureType type;

	public SpawnEnvironmentRule(Set<Environment> e, Set<World> worlds,
			CreatureType type) {
		this.spawnableEnvironment = e;
		this.worlds = worlds;
		this.type = type;
	}
	
	public SpawnEnvironmentRule(String data, Set<World> worlds,
			CreatureType type) {
		spawnableEnvironment = new HashSet<Environment>();
		String[] envString = data.split(" ");
		for (String s : envString)
		{
			spawnableEnvironment.add(Environment.valueOf(s));
		}
		this.worlds = worlds;
		this.type = type;
	}

	public boolean spawn(SpawnInfo info) {
		if (this.spawnableEnvironment.contains(info.getEnv())) {
			return true;
		}
		return false;
	}

	public boolean checkWorld(World world) {
		if (worlds.contains(world)) {
			return true;
		}
		return false;
	}

	public boolean checkCreatureType(CreatureType type) {
		if (this.type == type) {
			return true;
		}
		return false;
	}

	public CreatureType getCreatureType() {
		return type;
	}

	public Set<World> getWorlds() {
		return worlds;
	}

	public String getData() {
		String data = "";
		for (Environment e : spawnableEnvironment)
		{
			data += e.toString() + " ";
		}
		data.trim();
		return data;
	}

}
