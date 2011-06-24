package com.elBukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.plugins.crowd.Info;

/* 
 * A rule to control the time of creature spawning
 * 
 * @author Andrew Querol(winsock)
 */

public class SpawnTimeRule extends Rule {

	private Time spawnTime;

	public SpawnTimeRule(World world, CreatureType type, CrowdControlPlugin plugin) {
		super(world, type, plugin);
		this.ruleType = Type.Spawn;
	}

	@Override
	public boolean check(Info info) {

		if (plugin.getCreatureHandler(info.getLocation().getWorld()).isDay()) {
			switch (spawnTime) {
			case Day:
				return true;
			case Night:
				return false;
			}
		} else {
			switch (spawnTime) {
			case Day:
				return false;
			case Night:
				return true;
			}
		}

		return false;
	}

	@Override
	public String getData() {
		return spawnTime.toString();
	}

	@Override
	public void init(String data) {
		this.spawnTime = Time.valueOf(data);
	}

}
