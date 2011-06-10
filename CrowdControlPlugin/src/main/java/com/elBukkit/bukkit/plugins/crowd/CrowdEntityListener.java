package com.elBukkit.bukkit.plugins.crowd;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Fireball;
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
import com.elBukkit.bukkit.plugins.crowd.creature.Nature;
import com.elBukkit.bukkit.plugins.crowd.rules.Type;

/*
 * Entity listener, calls necessary rule checks 
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdEntityListener extends EntityListener {

	private CrowdControlPlugin plugin;
	private Random rand = new Random();

	public CrowdEntityListener(CrowdControlPlugin plugin) {
		this.plugin = plugin;
	}

	private Set<Info> pendingSpawn = new HashSet<Info>();

	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {

		for (Info i : pendingSpawn) {
			if (i.getID() == event.getEntity().getEntityId()) {
				pendingSpawn.remove(i);
				if (event.getEntity() instanceof Creature) {
					plugin.creatureHandler.addCreature((Creature) event
							.getEntity());
				}
				return;
			}
		}

		Info info = new Info();
		info.setEnv(event.getLocation().getWorld().getEnvironment());
		info.setLocation(event.getLocation());
		int random = rand.nextInt(CreatureType.values().length);
		info.setType(CreatureType.values()[random]);

		if (plugin.ruleHandler.passesRules(info, Type.Spawn)) {
			CreatureInfo cInfo = plugin.creatureHandler.getInfo(info.getType());
			if (rand.nextFloat() <= cInfo.getSpawnChance()) {
				pendingSpawn.add(info);
				info.spawn();
			}
		}

		event.setCancelled(true);
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getEntity() instanceof Creature) {
			Info info = new Info();
			info.setCreature((Creature) event.getEntity());
			info.setTarget(event.getTarget());
			info.setReason(event.getReason());

			CreatureInfo cInfo = plugin.creatureHandler
					.getInfo(plugin.creatureHandler.getCreatureType(event
							.getEntity()));
			if (event.getTarget() instanceof Player) {
				if (event.getReason() == TargetReason.FORGOT_TARGET) {
					plugin.creatureHandler.removeAttacked(info.getCreature(),
							(Player) event.getTarget());
				} else if (event.getReason() == TargetReason.TARGET_DIED) {
					plugin.creatureHandler.removeAttacked(info.getCreature(),
							(Player) event.getTarget());
				}
			}

			if (cInfo != null) {
				if (plugin.creatureHandler.isDay(event.getEntity().getWorld())) {
					switch (event.getReason()) {
					case CLOSEST_PLAYER:
						if (cInfo.getCreatureNatureDay() != Nature.Aggressive) {
							event.setCancelled(true);
						}
						break;
					case TARGET_ATTACKED_ENTITY:
						if (cInfo.getCreatureNatureDay() == Nature.Passive) {
							event.setCancelled(true);
						}
						break;
					}
				} else {
					switch (event.getReason()) {
					case CLOSEST_PLAYER:
						if (cInfo.getCreatureNatureDay() != Nature.Aggressive) {
							event.setCancelled(true);
						}
						break;
					case TARGET_ATTACKED_ENTITY:
						if (cInfo.getCreatureNatureDay() == Nature.Passive) {
							event.setCancelled(true);
						}
						break;
					}
				}

				if (!plugin.ruleHandler.passesRules(info, Type.Target)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@Override
	public void onEntityCombust(EntityCombustEvent event) {
		CreatureInfo cInfo = plugin.creatureHandler
				.getInfo(plugin.creatureHandler.getCreatureType(event
						.getEntity()));

		if (cInfo != null) {
			if (!cInfo.isBurnDay()) {
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent entityDmgEvent = (EntityDamageByEntityEvent) event;
				if (entityDmgEvent.getEntity() instanceof Player) {
					if (entityDmgEvent.getDamager() instanceof Creature) {
						plugin.creatureHandler.addAttacked(
								(Creature) entityDmgEvent.getDamager(),
								(Player) entityDmgEvent.getEntity());
						event.setCancelled(true);
						return;
					}
				} else if (event.getEntity() instanceof Creature) {
					Creature c = (Creature) event.getEntity();
					CreatureInfo cInfo = plugin.creatureHandler
							.getInfo(plugin.creatureHandler
									.getCreatureType(entityDmgEvent
											.getDamager()));

					if (cInfo != null) {
						plugin.creatureHandler.damageCreature(c,
								cInfo.getCollisionDamage());
					}
				}
			} else if (event instanceof EntityDamageByProjectileEvent) {
				EntityDamageByProjectileEvent entityProjectileEvent = (EntityDamageByProjectileEvent) event;

				if (entityProjectileEvent.getProjectile() instanceof Arrow) {
					CreatureInfo cInfo = plugin.creatureHandler
							.getInfo(CreatureType.SKELETON);

					if (cInfo != null) {
						if (event.getEntity() instanceof Creature) {
							Creature c = (Creature) event.getEntity();
							plugin.creatureHandler.damageCreature(c,
									cInfo.getMiscDamage());
						} else {
							event.setDamage(cInfo.getMiscDamage());
						}
					}
				} else if (entityProjectileEvent.getProjectile() instanceof Fireball) {
					CreatureInfo cInfo = plugin.creatureHandler
							.getInfo(CreatureType.GHAST);

					if (cInfo != null) {
						if (event.getEntity() instanceof Creature) {
							Creature c = (Creature) event.getEntity();
							plugin.creatureHandler.damageCreature(c,
									cInfo.getMiscDamage());
						} else {
							event.setDamage(cInfo.getMiscDamage());
						}
					}
				}
			}
		}

		if (event.getEntity() instanceof Creature) {
			if (plugin.creatureHandler.getHealth((Creature) event.getEntity()) <= 0) {
				plugin.creatureHandler.removeAllAttacked((Creature) event
						.getEntity());
			}
		} else if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (p.isDead() || (p.getHealth() - event.getDamage()) <= 0) {
				plugin.creatureHandler.removePlayer(p);
			}
		}
	}
}
