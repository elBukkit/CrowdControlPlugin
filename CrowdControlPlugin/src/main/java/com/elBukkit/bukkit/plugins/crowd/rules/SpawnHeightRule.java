package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * A rule that controls spawning based on height.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnHeightRule extends Rule {

	int min, max;
	
	public SpawnHeightRule(Set<World> worlds, CreatureType type) {
		super(worlds, type);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(String data)
	{
		// TODO Finish init()
	}

	public boolean spawn(Info info) {
		if (info.getLocation().getBlockY() > min) {
			if (info.getLocation().getBlockY() < max) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getData() {
		return String.valueOf(max) + "," + String.valueOf(min);
	}

}
