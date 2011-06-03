package com.elBukkit.mine.bukkit.plugins.crowd;

import org.bukkit.entity.Creature;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

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

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getEntity() instanceof Creature) {
			TargetInfo info = new TargetInfo();
			info.setCreature((Creature) event.getEntity());
			info.setTarget(event.getTarget());
			info.setReason(event.getReason());

			if (!plugin.ruleHandler.passesRules(info)) {
				event.setCancelled(true);
			}
		}
	}
}
