package com.elBukkit.bukkit.plugins.crowd;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.elBukkit.bukkit.plugins.crowd.creature.CreatureInfo;
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
        if (event.isCancelled())
            return;
        for (Info i : plugin.pendingSpawn) {
            if (i.getID() == event.getEntity().getEntityId()) {
                plugin.pendingSpawn.remove(i);
                if (event.getEntity() instanceof LivingEntity) {
                    plugin.getCreatureHandler(event.getLocation().getWorld()).addLivingEntity((LivingEntity) event.getEntity());
                }
                return;
            }
        }

        event.setCancelled(true);
    }

    @Override
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.isCancelled())
            return;
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
                if (event.getReason() == TargetReason.FORGOT_TARGET) {
                    plugin.getCreatureHandler(event.getEntity().getWorld()).removeAttacked((LivingEntity) info.getEntity(), (Player) event.getTarget());
                } else if (event.getReason() == TargetReason.TARGET_DIED) {
                    plugin.getCreatureHandler(event.getEntity().getWorld()).removeAttacked((LivingEntity) info.getEntity(), (Player) event.getTarget());
                }
            }

            event.setCancelled(true); // Targeting handled in the Damage Handler
        }
    }

    @Override
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.isCancelled())
            return;
        if (event.getEntity() instanceof LivingEntity) {
            CreatureInfo cInfo = plugin.getCreatureHandler(event.getEntity().getWorld()).getInfo(plugin.getCreatureHandler(event.getEntity().getWorld()).getCreatureType((LivingEntity) event.getEntity()));

            if (cInfo != null) {
                if (plugin.getCreatureHandler(event.getEntity().getWorld()).isDay(event.getEntity().getWorld()) && !cInfo.isBurnDay()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled())
            return;
        if (event.getCause() == DamageCause.ENTITY_ATTACK) {
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityDmgEvent = (EntityDamageByEntityEvent) event;

                if (entityDmgEvent.getDamager() instanceof Fireball) {
                    CreatureInfo cInfo = plugin.getCreatureHandler(event.getEntity().getWorld()).getInfo(CreatureType.GHAST);

                    if (cInfo != null) {
                        if (event.getEntity() instanceof LivingEntity) {
                            LivingEntity c = (LivingEntity) event.getEntity();
                            plugin.getCreatureHandler(event.getEntity().getWorld()).damageLivingEntity(c, cInfo.getMiscDamage());
                        } else {
                            event.setDamage(cInfo.getMiscDamage());
                        }
                    }
                } else if (entityDmgEvent.getDamager() instanceof Player) {
                    CreatureInfo cInfo = plugin.getCreatureHandler(event.getEntity().getWorld()).getInfo(plugin.getCreatureHandler(event.getEntity().getWorld()).getCreatureType((LivingEntity) entityDmgEvent.getEntity()));

                    if (cInfo != null) {
                        plugin.getCreatureHandler(event.getEntity().getWorld()).damageLivingEntity((LivingEntity) event.getEntity(), event.getDamage());
                    }
                }

                if (entityDmgEvent.getEntity() instanceof Player) {
                    if (entityDmgEvent.getDamager() instanceof LivingEntity) {
                        plugin.getCreatureHandler(event.getEntity().getWorld()).addAttacked((LivingEntity) entityDmgEvent.getDamager(), (Player) entityDmgEvent.getEntity());
                        event.setCancelled(true);
                        return;
                    }
                } else if (event.getEntity() instanceof LivingEntity) {
                    LivingEntity entity = (LivingEntity) event.getEntity();
                    CreatureInfo cInfo = plugin.getCreatureHandler(event.getEntity().getWorld()).getInfo(plugin.getCreatureHandler(event.getEntity().getWorld()).getCreatureType((LivingEntity) entityDmgEvent.getDamager()));

                    if (cInfo != null) {
                        plugin.getCreatureHandler(event.getEntity().getWorld()).damageLivingEntity(entity, cInfo.getCollisionDamage());
                        entity.damage(0);
                        event.setCancelled(true);
                    }
                }
            } else if (event instanceof EntityDamageByProjectileEvent) {
                EntityDamageByProjectileEvent entityProjectileEvent = (EntityDamageByProjectileEvent) event;
                if (entityProjectileEvent.getProjectile() instanceof Arrow) {
                    CreatureInfo cInfo = plugin.getCreatureHandler(event.getEntity().getWorld()).getInfo(CreatureType.SKELETON);
                    if (cInfo != null) {
                        if (event.getEntity() instanceof LivingEntity) {
                            LivingEntity entity = (LivingEntity) event.getEntity();
                            plugin.getCreatureHandler(event.getEntity().getWorld()).damageLivingEntity(entity, cInfo.getMiscDamage());
                            event.setCancelled(true);
                        } else {
                            entityProjectileEvent.setDamage(cInfo.getMiscDamage());
                        }
                    }
                } else if (entityProjectileEvent.getProjectile() instanceof Fireball) {
                    CreatureInfo cInfo = plugin.getCreatureHandler(event.getEntity().getWorld()).getInfo(CreatureType.GHAST);
                    if (cInfo != null) {
                        if (event.getEntity() instanceof LivingEntity) {
                            LivingEntity entity = (LivingEntity) event.getEntity();
                            plugin.getCreatureHandler(event.getEntity().getWorld()).damageLivingEntity(entity, cInfo.getMiscDamage());
                            event.setCancelled(true);
                        } else {
                            entityProjectileEvent.setDamage(cInfo.getMiscDamage());
                        }
                    }
                }
            }
        } else if (event.getCause() == DamageCause.FALL) {
            if (!(event.getEntity() instanceof Player)) {
                LivingEntity entity = (LivingEntity) event.getEntity();
                plugin.getCreatureHandler(event.getEntity().getWorld()).damageLivingEntity(entity, event.getDamage());
                entity.damage(0);
                event.setCancelled(true);
                System.out.println("Fall death");
            }
        }

        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (p.isDead() || (p.getHealth() - event.getDamage()) <= 0) {
                plugin.getCreatureHandler(event.getEntity().getWorld()).removePlayer(p);
            }
        }
    }
}
