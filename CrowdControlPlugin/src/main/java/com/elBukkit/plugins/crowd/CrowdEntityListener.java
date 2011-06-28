package com.elBukkit.plugins.crowd;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.elBukkit.plugins.crowd.creature.BaseInfo;
import com.elBukkit.plugins.crowd.creature.CreatureHandler;
import com.elBukkit.plugins.crowd.creature.CrowdCreature;
import com.elBukkit.plugins.crowd.rules.Type;

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
        if (event.isCancelled()) {
            return;
        }

        if (event.getSpawnReason() == SpawnReason.NATURAL) {
            event.setCancelled(true);
        } else {
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) event.getEntity();
                CreatureHandler cHandler = plugin.getCreatureHandler(event.getLocation().getWorld());
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

        CreatureHandler cHandler = plugin.getCreatureHandler(event.getEntity().getWorld());

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

        CreatureHandler cHandler = plugin.getCreatureHandler(event.getEntity().getWorld());

        if (event.getEntity() instanceof Player) {
            Player attacked = (Player) event.getEntity();

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
                    if (!(eventDmg.getDamager() instanceof Player)) {
                        LivingEntity e = (LivingEntity) eventDmg.getDamager();
                        attacker = cHandler.getCrowdCreature(e);
                        if (attacker != null) {
                            event.setDamage(attacker.getBaseInfo().getCollisionDamage());
                        }
                    }
                }
            } else {
                attacked.damage(event.getDamage());
            }

            if (attacked.isDead() || (attacked.getHealth() - event.getDamage()) <= 0) {
                plugin.getCreatureHandler(event.getEntity().getWorld()).removePlayer(attacked);
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
                            attacked.damage(attacker.getBaseInfo().getCollisionDamage());
                        }
                    } else {
                        attacked.damage(event.getDamage());
                    }
                }
            } else {
                attacked.damage(event.getDamage());
            }

            event.setCancelled(true);
        }
    }

    @Override
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.isCancelled()) {
            return;
        }

        CreatureHandler cHandler = plugin.getCreatureHandler(event.getEntity().getWorld());

        if (event.getEntity() instanceof LivingEntity) {
            Info info = new Info();
            info.setEntity((LivingEntity) event.getEntity());
            info.setTarget(event.getTarget());
            info.setReason(event.getReason());

            if (event.getReason() == TargetReason.CUSTOM) {
                if (!plugin.ruleHandler.passesRules(info, Type.Target)) {
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
