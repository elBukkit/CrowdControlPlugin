package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.SpawnInfo;

/*
 * A rule that controls spawning based on height.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnHeightRule implements SpawnRule {

	private Set<World> worlds;
	private CreatureType type;

	int min, max;

	public SpawnHeightRule(int min, int max, Set<World> worlds,
			CreatureType type) {
		this.worlds = worlds;
		this.type = type;
		
		this.min = min;
		this.max = max;
	}
	
	public SpawnHeightRule(String data, Set<World> worlds,
			CreatureType type) {
		this.worlds = worlds;
		this.type = type;
		
		String[] splitData = data.split(",");
		max = Integer.parseInt(splitData[0]);
		min = Integer.parseInt(splitData[0]);
	}

	public boolean spawn(SpawnInfo info) {
		if (info.getLocation().getBlockY() > min) {
			if (info.getLocation().getBlockY() < max) {
				return true;
			}
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
		return this.type;
	}

	public Set<World> getWorlds() {
		return this.worlds;
	}

	public String getData() {
		return String.valueOf(max) + "," + String.valueOf(min);
	}

}
