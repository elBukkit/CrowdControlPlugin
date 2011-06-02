package com.elmakers.mine.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;

import com.elmakers.mine.bukkit.plugins.crowd.SpawnInfo;

public class EnvironmentRule implements Rule {

	private Set<Environment> spawnableEnvironment;
	private Set<World> worlds;
	private CreatureType type;
	
	public EnvironmentRule(Set<Environment> e, Set<World> worlds, CreatureType type){
		this.spawnableEnvironment = e;
		this.worlds = worlds;
		this.type = type;
	}
	
	public boolean spawn(SpawnInfo info) {
		if (this.spawnableEnvironment.contains(info.getEnv())){
			return true;
		}
		return false;
	}

	public boolean checkWorld(World world) {
		if (worlds.contains(world)){
			return true;
		}
		return false;
	}

	public boolean checkCreatureType(CreatureType type) {
		if (this.type == type){
			return true;
		}
		return false;
	}

}
