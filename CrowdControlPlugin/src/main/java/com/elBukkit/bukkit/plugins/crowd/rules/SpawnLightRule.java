package com.elBukkit.bukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * A rule that controls spawning based on light levels.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnLightRule extends Rule {

	private int min, max;

	public SpawnLightRule(World world, CreatureType type) {
		super(world, type);
		this.ruleType = Type.Spawn;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(String data) {
		// TODO Finish init()
	}

	@Override
	public boolean check(Info info) {
		if (info.getLocation().getBlock().getLightLevel() > min) {
			if (info.getLocation().getBlock().getLightLevel() < max) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
