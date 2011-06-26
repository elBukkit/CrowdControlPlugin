package com.elBukkit.plugins.crowd;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

import com.elBukkit.plugins.crowd.creature.CreatureHandler;
import com.elBukkit.plugins.crowd.creature.CrowdCreature;

public class CrowdWorldListener extends WorldListener {
	
	private CrowdControlPlugin plugin;
	
	public CrowdWorldListener(CrowdControlPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onChunkUnload(ChunkUnloadEvent event) {
		
		CreatureHandler handler = plugin.getCreatureHandler(event.getWorld());
		
		if (event.getChunk().getEntities().length > 0) {
			for (Entity e : event.getChunk().getEntities()) {
				if ((e instanceof LivingEntity) && !(e instanceof Player)) {
					CrowdCreature c = handler.getCrowdCreature((LivingEntity)e);
					
					if (c != null) {
						handler.despawn(c);
					}
				}
			}
		}
	}
}
