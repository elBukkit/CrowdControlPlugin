package com.elbukkit.plugins.crowd;

import net.minecraft.server.EntityHuman;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.elbukkit.plugins.crowd.creature.BaseInfo;
import com.elbukkit.plugins.crowd.creature.CreatureHandler;
import com.elbukkit.plugins.crowd.creature.CrowdCreature;

/**
 * Entity listener, calls necessary rule checks
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class CrowdEntityListener extends EntityListener {
    
    private final CrowdControlPlugin plugin;
    
    public CrowdEntityListener(CrowdControlPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        if (event.getSpawnReason() == SpawnReason.NATURAL) {
            event.setCancelled(true);
        } else {
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) event.getEntity();
                CreatureHandler cHandler = this.plugin.getCreatureHandler(event.getLocation().getWorld());
                BaseInfo info = cHandler.getBaseInfo(event.getCreatureType());
                if (info != null) {
                    cHandler.addCrowdCreature(new CrowdCreature(entity, event.getCreatureType(), info));
                }
            }
        }
    }
    
    @Override
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        CreatureHandler cHandler = this.plugin.getCreatureHandler(event.getEntity().getWorld());
        
        if (event.getEntity() instanceof LivingEntity) {
            CrowdCreature c = cHandler.getCrowdCreature((LivingEntity) event.getEntity());
            
            if (c != null) {
                if (!cHandler.shouldBurn(c.getEntity().getLocation()) && !c.getBaseInfo().isBurnDay()) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        if (event.getDamage() <= 0) {
            return;
        }
        
        CreatureHandler cHandler = this.plugin.getCreatureHandler(event.getEntity().getWorld());
        
        if (cHandler == null) {
            return;
        }
        
        if (event.getEntity() instanceof LivingEntity) {
            
            LivingEntity attacked = (LivingEntity) event.getEntity();
            
            if (event instanceof EntityDamageByEntityEvent) {
                Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
                
                if (entity instanceof LivingEntity) {
                    LivingEntity attacker = (LivingEntity) entity;
                    
                    CrowdCreature crowdAttacker = cHandler.getCrowdCreature(attacker);
                    
                    if (crowdAttacker != null) {
                        event.setDamage(crowdAttacker.getDamage());
                    }
                }
            }
            
            if (this.plugin.getSlimeSplit() && ((attacked.getHealth() <= 0) || attacked.isDead()) && (attacked instanceof Slime)) {
                Slime slime = (Slime) attacked;
                
                if (slime.getSize() > 1) {
                    this.plugin.getCreatureHandler(slime.getWorld()).despawn(cHandler.getCrowdCreature(slime));
                    for (int i = 0; i < 4; i++) {
                        Slime slimeSmall = (Slime) slime.getWorld().spawnCreature(slime.getLocation(), CreatureType.SLIME);
                        slimeSmall.setSize(slime.getSize() - 1);
                    }
                }
            }
        }
    }
    
    @Override
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        CreatureHandler cHandler = this.plugin.getCreatureHandler(event.getEntity().getWorld());
        
        if (event.getEntity() instanceof LivingEntity) {
            Info info = new Info(this.plugin);
            info.setEntity((LivingEntity) event.getEntity());
            info.setTarget(event.getTarget());
            info.setReason(event.getReason());
            
            if (event.getReason() == TargetReason.CUSTOM) {
                return;
            }
            
            if (event.getTarget() instanceof Player) {
                CrowdCreature c = cHandler.getCrowdCreature(info.getEntity());
                if (event.getReason() == TargetReason.FORGOT_TARGET) {
                    cHandler.removeAttacked(c, (Player) event.getTarget());
                } else if (event.getReason() == TargetReason.TARGET_DIED) {
                    cHandler.removePlayer((Player) event.getTarget());
                } else if (event.getReason() == TargetReason.TARGET_ATTACKED_ENTITY) {
                    cHandler.addAttacked(c, (Player) event.getTarget());
                }
            }
            
            event.setCancelled(true);
        }
    }
}
