package com.elmakers.mine.bukkit.plugins.crowd;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;

/*
 * Entity listener, calls necessary rule checks 
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdEntityListener extends EntityListener {
	private CrowdControlPlugin plugin;

	public CrowdEntityListener(CrowdControlPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		SpawnInfo info = new SpawnInfo();
		info.setEnv(event.getLocation().getWorld().getEnvironment());
		info.setLocation(event.getLocation());
		info.setType(event.getCreatureType());

		if (!plugin.ruleHandler.passesRules(info)) {
			event.setCancelled(true);
		}
	}
}
