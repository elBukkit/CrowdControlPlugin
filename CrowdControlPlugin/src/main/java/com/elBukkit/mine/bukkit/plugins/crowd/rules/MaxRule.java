package com.elBukkit.mine.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.mine.bukkit.plugins.crowd.SpawnInfo;

/*
 * TODO Finish the rule
 * 
 * Set maximum number of a creature type
 * 
 *  @author Andrew Querol(WinSock)
 */

public class MaxRule implements SpawnRule {

	private Set<World> worlds;
	private CreatureType type;

	public MaxRule(Set<World> worlds, CreatureType type) {
		this.worlds = worlds;
		this.type = type;
	}

	public boolean spawn(SpawnInfo info) {
		// TODO Auto-generated method stub
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
