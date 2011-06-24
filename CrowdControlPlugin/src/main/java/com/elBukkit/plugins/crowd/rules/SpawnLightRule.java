package com.elBukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.plugins.crowd.Info;

/*
 * A rule that controls spawning based on light levels.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnLightRule extends Rule {

	private int min, max;

	public SpawnLightRule(World world, CreatureType type, CrowdControlPlugin plugin) {
		super(world, type, plugin);
		this.ruleType = Type.Spawn;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean check(Info info) {
		if (info.getLocation().getBlock().getLightLevel() >= min) {
			if (info.getLocation().getBlock().getLightLevel() <= max) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getData() {
		return String.valueOf(max) + "," + String.valueOf(min);
	}

	@Override
	public void init(String data) {
		String[] split = data.split(",");
		this.max = Integer.parseInt(split[0]);
		this.min = Integer.parseInt(split[1]);
	}

}
