package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.SpawnInfo;

/*
 * A rule that controls spawning based on light levels.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnLightRule implements SpawnRule {

	private Set<World> worlds;
	private CreatureType type;

	private int min, max;

	public SpawnLightRule(int min, int max, Set<World> worlds, CreatureType type) {
		this.worlds = worlds;
		this.type = type;

		this.min = min;
		this.max = max;
	}

	public boolean spawn(SpawnInfo info) {
		if (info.getLocation().getBlock().getLightLevel() > min) {
			if (info.getLocation().getBlock().getLightLevel() < max) {
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
		// TODO Auto-generated method stub
		return null;
	}

	public Set<World> getWorlds() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
