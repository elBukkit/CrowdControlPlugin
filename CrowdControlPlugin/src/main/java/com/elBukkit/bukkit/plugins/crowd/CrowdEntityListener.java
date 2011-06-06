package com.elBukkit.bukkit.plugins.crowd;

import org.bukkit.entity.Creature;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.elBukkit.bukkit.plugins.crowd.rules.Type;

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
		Info info = new Info();
		info.setEnv(event.getLocation().getWorld().getEnvironment());
		info.setLocation(event.getLocation());
		info.setType(event.getCreatureType());

		if (!plugin.ruleHandler.passesRules(info, Type.Spawn)) {
			event.setCancelled(true);
		}
		
		if(info.getType() != event.getCreatureType())
		{
			event.setCancelled(true);
			event.getLocation().getWorld().spawnCreature(info.getLocation(), info.getType());
		}
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getEntity() instanceof Creature) {
			Info info = new Info();
			info.setCreature((Creature) event.getEntity());
			info.setTarget(event.getTarget());
			info.setReason(event.getReason());

			if (!plugin.ruleHandler.passesRules(info, Type.Target)) {
				event.setCancelled(true);
			}
		}
	}
}
