package com.elBukkit.bukkit.plugins.crowd;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

import com.elBukkit.bukkit.plugins.crowd.creature.CreatureHandler;
import com.elBukkit.bukkit.plugins.crowd.creature.CrowdCreature;
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
		if (event.isCancelled()) {
			return;
		}

		if (event.getSpawnReason() == SpawnReason.NATURAL) {
			event.setCancelled(true);
		} else {
			if (event.getEntity() instanceof LivingEntity) {
				plugin.getCreatureHandler(event.getLocation().getWorld()).getCrowdCreature((LivingEntity) event.getEntity());
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
			CrowdCreature cInfo = cHandler.getCrowdCreature((LivingEntity) event.getEntity());

			if (cInfo != null) {
				if (cHandler.isDay() && !cInfo.isBurnDay()) {
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

		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent entityDmgEvent = (EntityDamageByEntityEvent) event;

				if (entityDmgEvent instanceof EntityDamageByProjectileEvent) {
					EntityDamageByProjectileEvent entityProjectileEvent = (EntityDamageByProjectileEvent) event;
					if (entityProjectileEvent.getProjectile() instanceof Arrow) {
						CrowdCreature c = cHandler.getBaseInfo(CreatureType.SKELETON);
						if (c != null) {
							if (event.getEntity() instanceof LivingEntity) {
								CrowdCreature cAttacked = cHandler.getCrowdCreature((LivingEntity) event.getEntity());
								cAttacked.damage(c.getMiscDamage());
								event.setCancelled(true);
							} else {
								entityProjectileEvent.setDamage(c.getMiscDamage());
							}
						}
					} else if (entityProjectileEvent.getProjectile() instanceof Fireball) {
						CrowdCreature cInfo = cHandler.getBaseInfo(CreatureType.GHAST);
						if (cInfo != null) {
							if (event.getEntity() instanceof LivingEntity) {
								LivingEntity entity = (LivingEntity) event.getEntity();
								CrowdCreature c = cHandler.getCrowdCreature(entity);
								c.damage(cInfo.getMiscDamage());
								event.setCancelled(true);
							} else {
								entityProjectileEvent.setDamage(cInfo.getMiscDamage());
							}
						}
					}
				}

				if (entityDmgEvent.getDamager() instanceof Player) {
					CrowdCreature c = cHandler.getCrowdCreature((LivingEntity) entityDmgEvent.getEntity());

					if (c != null) {
						c.damage(event.getDamage());
					}
				}

				if (entityDmgEvent.getEntity() instanceof Player) {
					if (entityDmgEvent.getDamager() instanceof LivingEntity) {
						CrowdCreature c = cHandler.getCrowdCreature((LivingEntity) entityDmgEvent.getDamager());
						cHandler.addAttacked(c, (Player) entityDmgEvent.getEntity());
						event.setCancelled(true);
						return;
					}
				} else if (event.getEntity() instanceof LivingEntity) {
					LivingEntity entity = (LivingEntity) event.getEntity();
					CrowdCreature cAttacked = cHandler.getCrowdCreature(entity);
					CrowdCreature cInfo = cHandler.getCrowdCreature((LivingEntity) entityDmgEvent.getDamager());

					if (cInfo != null) {
						cAttacked.damage(cInfo.getCollisionDamage());
						entity.damage(0);
						event.setCancelled(true);
					}
				}
			}
		} else {
			if (!(event.getEntity() instanceof Player)) {
				LivingEntity entity = (LivingEntity) event.getEntity();
				CrowdCreature attacker = cHandler.getCrowdCreature(entity);
				attacker.damage(event.getDamage());
				entity.damage(0);
				event.setCancelled(true);
			}
		}

		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (p.isDead() || (p.getHealth() - event.getDamage()) <= 0) {
				cHandler.removePlayer(p);
			}
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
					cHandler.removeAttacked(c, (Player) event.getTarget());
				}
			}

			event.setCancelled(true); // Targeting handled in the Damage Handler
		}
	}
}
