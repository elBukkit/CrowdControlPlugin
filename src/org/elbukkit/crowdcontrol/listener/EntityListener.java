package org.elbukkit.crowdcontrol.listener;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.elbukkit.crowdcontrol.CrowdControlPlugin;
import org.elbukkit.crowdcontrol.entity.CreatureType;
import org.elbukkit.crowdcontrol.entity.EntityData;
import org.elbukkit.crowdcontrol.entity.EntityInstance;

public class EntityListener implements Listener {

    CrowdControlPlugin plugin;

    public EntityListener(CrowdControlPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!plugin.getSettingManager().getMasterSettings().isEnabledWorld(event.getLocation().getWorld())) {
            return;
        }
        
        if (event.getSpawnReason() == SpawnReason.NATURAL) {
            event.setCancelled(true);
        }
        
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityCombust(EntityCombustEvent event) {
        Entity e = event.getEntity();
        
        if (event.isCancelled()) {
            return;
        }
        
        if (!plugin.getSettingManager().getMasterSettings().isEnabledWorld(event.getEntity().getWorld())) {
            return;
        }

        if (e instanceof LivingEntity) {

            if (e instanceof Player) {
                return;
            }

            CreatureType type = CreatureType.creatureTypeFromEntity(e);
            EntityData data = plugin.getSettingManager().getSetting(type, e.getWorld());

            if (!data.isBurnDay()) {
                if (isDay(e.getWorld())) {
                    if (e.getLocation().getBlock().getLightFromSky() > 7) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        
        if (event.isCancelled()) {
            return;
        }
        
        if (!plugin.getSettingManager().getMasterSettings().isEnabledWorld(event.getEntity().getWorld())) {
            return;
        }
        
        if (event.getEntity() instanceof LivingEntity) {
            if (event.getEntity() instanceof Player) {
                return;
            }
            
            LivingEntity le = (LivingEntity) event.getEntity();
            plugin.getCreatureController().getInstance(le).damage(event.getDamage());
            
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent)event;
                
                le.damage(0, entityDamageByEntityEvent.getDamager());
            }
            
            event.setDamage(0);
        }
        
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTame(EntityTameEvent event) {
        
        if (event.isCancelled()) {
            return;
        }
        
        if (!plugin.getSettingManager().getMasterSettings().isEnabledWorld(event.getEntity().getWorld())) {
            return;
        }
        
        if (event.getEntity() instanceof Tameable) {
            EntityInstance instance = plugin.getCreatureController().getInstance((LivingEntity) event.getEntity());
            if (instance.getDefaultData() instanceof org.elbukkit.crowdcontrol.entity.Tameable) {
                org.elbukkit.crowdcontrol.entity.Tameable data = (org.elbukkit.crowdcontrol.entity.Tameable)instance.getDefaultData();
                instance.setHealth(data.getTammedHealth());
            }
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        
        if (!plugin.getSettingManager().getMasterSettings().isEnabledWorld(event.getEntity().getWorld())) {
            return;
        }
        
        if (event instanceof LivingEntity) {
            plugin.getCreatureController().removeEntity((LivingEntity) event.getEntity());
        }
    }

    public boolean isDay(World w) {
        if ((w.getTime() > 0) && (w.getTime() < 12000)) {
            return true;
        }
        return false;
    }
}
