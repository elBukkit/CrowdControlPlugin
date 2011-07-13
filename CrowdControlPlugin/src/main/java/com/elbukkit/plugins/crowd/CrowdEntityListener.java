package com.elbukkit.plugins.crowd;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.elbukkit.plugins.crowd.creature.BaseInfo;
import com.elbukkit.plugins.crowd.creature.CreatureHandler;
import com.elbukkit.plugins.crowd.creature.CrowdCreature;
import com.elbukkit.plugins.crowd.rules.Type;

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
        
        CreatureHandler cHandler = this.plugin.getCreatureHandler(event.getEntity().getWorld());
        
        if (cHandler == null) {
            return;
        }
        
        if (event.getEntity() instanceof Player) {
            Player attacked = (Player) event.getEntity();
            
            if (attacked.getNoDamageTicks() > 0) {
                return;
            }
            
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent eventDmg = (EntityDamageByEntityEvent) event;
                CrowdCreature attacker = null;
                
                if (eventDmg instanceof EntityDamageByProjectileEvent) {
                    EntityDamageByProjectileEvent eventP = (EntityDamageByProjectileEvent) eventDmg;
                    Projectile p = eventP.getProjectile();
                    
                    attacker = cHandler.getCrowdCreature(p.getShooter());
                    if (attacker != null) {
                        event.setDamage(attacker.getBaseInfo().getMiscDamage());
                    }
                    
                } else {
                    if (!(eventDmg.getDamager() instanceof Player) && (eventDmg.getDamager() instanceof LivingEntity)) {
                        LivingEntity e = (LivingEntity) eventDmg.getDamager();
                        attacker = cHandler.getCrowdCreature(e);
                        if (attacker != null) {
                            if (event.getCause() != DamageCause.ENTITY_ATTACK) {
                                event.setDamage(attacker.getBaseInfo().getMiscDamage());
                            } else {
                                event.setDamage(attacker.getCollisionDamage());
                            }
                        }
                    }
                }
            }
            
            if (attacked.isDead() || ((attacked.getHealth() - event.getDamage()) <= 0)) {
                this.plugin.getCreatureHandler(event.getEntity().getWorld()).removePlayer(attacked);
            }
            
        } else if (event.getEntity() instanceof LivingEntity) {
            LivingEntity e = (LivingEntity) event.getEntity();
            CrowdCreature attacked = cHandler.getCrowdCreature(e);
            
            if (attacked == null) {
                return;
            }
            
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent eventDmg = (EntityDamageByEntityEvent) event;
                
                if (eventDmg instanceof EntityDamageByProjectileEvent) {
                    EntityDamageByProjectileEvent eventP = (EntityDamageByProjectileEvent) eventDmg;
                    Projectile p = eventP.getProjectile();
                    
                    CrowdCreature attacker = cHandler.getCrowdCreature(p.getShooter());
                    if (attacker != null) {
                        attacked.damage(attacker.getBaseInfo().getMiscDamage());
                    }
                } else {
                    if (eventDmg.getDamager() instanceof Player) {
                        Player attacker = (Player) eventDmg.getDamager();
                        attacked.damage(event.getDamage());
                        cHandler.addAttacked(attacked, attacker);
                    } else if (eventDmg.getDamager() instanceof LivingEntity) {
                        LivingEntity ea = (LivingEntity) eventDmg.getDamager();
                        CrowdCreature attacker = cHandler.getCrowdCreature(ea);
                        
                        if (attacker != null) {
                            attacked.damage(attacker.getCollisionDamage());
                        }
                    } else {
                        attacked.damage(event.getDamage());
                    }
                }
            } else {
                attacked.damage(event.getDamage());
            }
            
            if (this.plugin.getSlimeSplit() && attacked.isDead() && (attacked.getType() == CreatureType.SLIME)) {
                Slime slime = (Slime) attacked.getEntity();
                
                if (slime.getSize() > 1) {
                    this.plugin.getCreatureHandler(slime.getWorld()).despawn(attacked);
                    for (int i = 0; i < 4; i++) {
                        Slime slimeSmall = (Slime) slime.getWorld().spawnCreature(slime.getLocation(), CreatureType.SLIME);
                        slimeSmall.setSize(slime.getSize() - 1);
                    }
                }
            }
            
            event.setCancelled(true);
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
                if (!this.plugin.getRuleHandler(event.getEntity().getWorld()).passesRules(info, Type.TARGET)) {
                    event.setCancelled(true);
                }
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
            
            event.setCancelled(true); // Targeting handled in the Damage Handler
        }
    }
}
