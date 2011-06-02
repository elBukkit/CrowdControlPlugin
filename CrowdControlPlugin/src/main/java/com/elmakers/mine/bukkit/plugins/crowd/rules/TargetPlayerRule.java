package com.elmakers.mine.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import com.elmakers.mine.bukkit.plugins.crowd.TargetInfo;

/*
 * A rule that prevents creatures from targeting certian players.
 * 
 * TODO Finish the code
 * 
 * @author Andrew Querol(WinSock)
 */

public class TargetPlayerRule implements TargetRule {

	private Set<World> worlds;
	private CreatureType type;

	public TargetPlayerRule(Set<Player> players, boolean targetable,Set<World> worlds, CreatureType type) {
		this.worlds = worlds;
		this.type = type;
	}

	public boolean target(TargetInfo info) {
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
