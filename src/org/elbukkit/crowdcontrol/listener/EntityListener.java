package org.elbukkit.crowdcontrol.listener;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.elbukkit.crowdcontrol.CrowdControlPlugin;
import org.elbukkit.crowdcontrol.entity.CreatureType;
import org.elbukkit.crowdcontrol.entity.EntityData;
import org.elbukkit.crowdcontrol.settings.SettingManager;

public class EntityListener implements Listener {

	CrowdControlPlugin plugin;
	SettingManager manager;
	
	public EntityListener(CrowdControlPlugin plugin) {
		this.plugin = plugin;
		this.manager = new SettingManager(plugin);
	}
	
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled())
            return;

        if (event.getSpawnReason() == SpawnReason.NATURAL) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityCombust(EntityCombustEvent event) {
    	Entity e = event.getEntity();
    	
    	if (e instanceof LivingEntity) {
    		if (e instanceof Player) {
    			return;
    		}
    		
    		CreatureType type = CreatureType.creatureTypeFromEntity((LivingEntity)e);
    		EntityData data = manager.getSetting(type, e.getWorld());
    		
    		if (!data.isBurnDay()) {
    			if (isDay(e.getWorld())) {
    				event.setCancelled(true);
    			}
    		}
    	}
    }
    
    public boolean isDay(World w) {
    	if (w.getTime() > 0 && w.getTime() < 12000) {
    		return true;
    	}
    	return false;
    }
}
