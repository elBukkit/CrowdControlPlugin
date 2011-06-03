package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import com.elBukkit.bukkit.plugins.crowd.TargetInfo;

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

	private Set<Player> players;
	private boolean targetable;

	public TargetPlayerRule(Set<Player> players, boolean targetable,
			Set<World> worlds, CreatureType type) {
		this.worlds = worlds;
		this.type = type;

		this.players = players;
		this.targetable = targetable;
	}

	public boolean target(TargetInfo info) {
		if (!targetable) {
			if (info.getTarget() instanceof Player) {
				Player pTarget = (Player) info.getTarget();
				if (players.contains(pTarget)) {
					return false;
				}
			}
		}
		return true;
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
