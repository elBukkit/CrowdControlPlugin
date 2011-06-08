package com.elBukkit.bukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;

import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * TODO Finish the rule
 * 
 * Set maximum number of a creature type
 * 
 *  @author Andrew Querol(WinSock)
 */

public class MaxRule extends Rule {

	private int maxMobs;

	public MaxRule(World world, CreatureType type) {
		super(world, type);
		this.ruleType = Type.Spawn;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(String data) {
		this.maxMobs = Integer.parseInt(data);
	}

	@Override
	public boolean check(Info info) {
		int number = 0;
		for (Entity e : info.getLocation().getWorld().getEntities()) {
			if (this.getCreatureType(e).equals(this.type)) {
				number++;
			}
		}

		if (number < maxMobs) {
			return true;
		}

		return false;
	}

	@Override
	public String getData() {
		return String.valueOf(maxMobs);
	}

}
