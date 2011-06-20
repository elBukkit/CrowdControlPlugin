package com.elBukkit.bukkit.plugins.crowd.events;

import java.util.EventListener;

public interface CrowdListener extends EventListener {
	public void onCreatureAttack(CreatureAttackEvent event);

	public void onCreatureMove(CreatureMoveEvent event);

	public void onCreatureSpawn(CreatureSpawnEvent event);
}
