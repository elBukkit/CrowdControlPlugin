package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * A rule that prevents creatures from targeting certian players.
 * 
 * @author Andrew Querol(WinSock)
 */

public class TargetPlayerRule extends Rule {

	private Set<String> players;
	private boolean targetable;

	public TargetPlayerRule(Set<World> worlds, CreatureType type) {
		super(worlds, type);
		this.ruleType = Type.Target;
	}

	@Override
	public void init(String data) {
		// TODO Finish init()
	}

	@Override
	public boolean check(Info info) {
		if (!targetable) {
			if (info.getTarget() instanceof Player) {
				Player pTarget = (Player) info.getTarget();
				if (players.contains(pTarget.getName())) {
					return false;
				}
			}
		}
		return true;
	}

	public String getData() {
		String data = "";
		for (String s : this.players) {
			data += s + ",";
		}
		data += ";" + String.valueOf(targetable);
		return data;
	}

}
