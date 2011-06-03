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

}
