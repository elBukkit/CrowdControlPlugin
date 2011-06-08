package com.elBukkit.bukkit.plugins.crowd.rules;

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

	private String player;
	private boolean targetable;

	public TargetPlayerRule(World world, CreatureType type) {
		super(world, type);
		this.ruleType = Type.Target;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(String data) {
		String[] split = data.split(",");
		this.player = split[0];
		this.targetable = Boolean.valueOf(split[1]);
	}

	@Override
	public boolean check(Info info) {
		if (!targetable) {
			if (info.getTarget() instanceof Player) {
				Player pTarget = (Player) info.getTarget();
				if (player.equalsIgnoreCase(pTarget.getName())) {
					return false;
				}
			}
		}
		return true;
	}

	public String getData() {
		return player + "," + String.valueOf(targetable);
	}

}
