package com.elmakers.mine.bukkit.plugins.crowd;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;

/*
 * Entity listener, overrides the default entity spawning.
 * 
 * @author Andrew Querol(WinSock)
 * 
 */

public class CrowdEntityListener extends EntityListener {
	private CrowdControlPlugin plugin;
	private Random generator = new Random();
	private Set<SpawnInfo> spawnEntities = new HashSet<SpawnInfo>();

	public CrowdEntityListener(CrowdControlPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			for (SpawnInfo i : spawnEntities) {
				if (i.getEntity() == (LivingEntity)event.getEntity()) {
					spawnEntities.remove(i);
					return; // Entity spawned by us, do not re-randomize the creature
				}
			}

			SpawnInfo info = new SpawnInfo();
			info.setEnv(event.getLocation().getWorld().getEnvironment());
			info.setLocation(event.getLocation());

			// Pick a random creature overriding default spawn
			int random = generator.nextInt(CreatureType.values().length);
			info.setType(CreatureType.values()[random]);

			if (plugin.ruleHandler.passesRules(info)) {
				info.spawn();
				if (info.getEntity() != null)
				{
					spawnEntities.add(info);
				}
			}

			event.setCancelled(true);
		}
	}
}
