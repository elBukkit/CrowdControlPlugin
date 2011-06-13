package com.elBukkit.bukkit.plugins.crowd;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
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
				if (event.getEntity() instanceof LivingEntity) {
					plugin.getCreatureHandler(event.getLocation().getWorld())
							.addLivingEntity((LivingEntity) event.getEntity());
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
			CreatureInfo cInfo = plugin.getCreatureHandler(
					event.getLocation().getWorld()).getInfo(info.getType());
			if (rand.nextFloat() <= cInfo.getSpawnChance()) {
				if (info.getType() == CreatureType.GIANT) {
					for (int i = 0; i < 10; i++) {
						Block b = info
								.getLocation()
								.getWorld()
								.getBlockAt(info.getLocation().getBlockX(),
										info.getLocation().getBlockY() + i,
										info.getLocation().getBlockZ());
						if (b.getType() != Material.AIR
								|| b.getType() != Material.WATER) {
							return; // Not enough room for the giant.
						}
					}
				}
				if (plugin.getCreatureHandler(event.getLocation().getWorld())
						.getCreatureCount() < plugin.maxPerWorld) {
					pendingSpawn.add(info);
					info.spawn();
				}
			}
		}

		event.setCancelled(true);
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
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
					plugin.getCreatureHandler(event.getEntity().getWorld())
							.removeAttacked((LivingEntity) info.getEntity(),
									(Player) event.getTarget());
				} else if (event.getReason() == TargetReason.TARGET_DIED) {
					plugin.getCreatureHandler(event.getEntity().getWorld())
							.removeAttacked((LivingEntity) info.getEntity(),
									(Player) event.getTarget());
				}
			}

			event.setCancelled(true); // Targeting handled in the Damage Handler
		}
	}

	@Override
	public void onEntityCombust(EntityCombustEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			CreatureInfo cInfo = plugin.getCreatureHandler(
					event.getEntity().getWorld()).getInfo(
					plugin.getCreatureHandler(event.getEntity().getWorld())
							.getCreatureType((LivingEntity) event.getEntity()));

			if (cInfo != null) {
				if (plugin.getCreatureHandler(event.getEntity().getWorld())
						.isDay(event.getEntity().getWorld())
						&& !cInfo.isBurnDay()) {
					event.setCancelled(true);
				}
			}
		}
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent entityDmgEvent = (EntityDamageByEntityEvent) event;

				if (entityDmgEvent.getDamager() instanceof Fireball) {
					CreatureInfo cInfo = plugin.getCreatureHandler(
							event.getEntity().getWorld()).getInfo(
							CreatureType.GHAST);

					if (cInfo != null) {
						if (event.getEntity() instanceof LivingEntity) {
							LivingEntity c = (LivingEntity) event.getEntity();
							plugin.getCreatureHandler(
									event.getEntity().getWorld())
									.damageLivingEntity(c,
											cInfo.getMiscDamage());
						} else {
							event.setDamage(cInfo.getMiscDamage());
						}
					}
				} else if (entityDmgEvent.getDamager() instanceof Player) {
					CreatureInfo cInfo = plugin.getCreatureHandler(
							event.getEntity().getWorld()).getInfo(
							plugin.getCreatureHandler(
									event.getEntity().getWorld())
									.getCreatureType(
											(LivingEntity) entityDmgEvent
													.getEntity()));

					if (cInfo != null) {
						plugin.getCreatureHandler(event.getEntity().getWorld())
								.damageLivingEntity(
										(LivingEntity) event.getEntity(),
										event.getDamage());
					}
				}

				if (entityDmgEvent.getEntity() instanceof Player) {
					if (entityDmgEvent.getDamager() instanceof LivingEntity) {
						plugin.getCreatureHandler(event.getEntity().getWorld())
								.addAttacked(
										(LivingEntity) entityDmgEvent
												.getDamager(),
										(Player) entityDmgEvent.getEntity());
						event.setCancelled(true);
						return;
					}
				} else if (event.getEntity() instanceof LivingEntity) {
					LivingEntity entity = (LivingEntity) event.getEntity();
					CreatureInfo cInfo = plugin.getCreatureHandler(
							event.getEntity().getWorld()).getInfo(
							plugin.getCreatureHandler(
									event.getEntity().getWorld())
									.getCreatureType(
											(LivingEntity) entityDmgEvent
													.getDamager()));

					if (cInfo != null) {
						plugin.getCreatureHandler(event.getEntity().getWorld())
								.damageLivingEntity(entity,
										cInfo.getCollisionDamage());
						event.setCancelled(true);
					}
				}
			} else if (event instanceof EntityDamageByProjectileEvent) {
				EntityDamageByProjectileEvent entityProjectileEvent = (EntityDamageByProjectileEvent) event;
				System.out.println("Projectile damage");
				if (entityProjectileEvent.getProjectile() instanceof Arrow) {
					CreatureInfo cInfo = plugin.getCreatureHandler(
							event.getEntity().getWorld()).getInfo(
							CreatureType.SKELETON);
					System.out.println("Arrow");
					if (cInfo != null) {
						if (event.getEntity() instanceof LivingEntity) {
							LivingEntity entity = (LivingEntity) event
									.getEntity();
							plugin.getCreatureHandler(
									event.getEntity().getWorld())
									.damageLivingEntity(entity,
											cInfo.getMiscDamage());
							event.setCancelled(true);
						} else {
							System.out.println("Player hit");
							entityProjectileEvent.setDamage(cInfo
									.getMiscDamage());
						}
					}
				} else if (entityProjectileEvent.getProjectile() instanceof Fireball) {
					CreatureInfo cInfo = plugin.getCreatureHandler(
							event.getEntity().getWorld()).getInfo(
							CreatureType.GHAST);
					System.out.println("Fireball");
					if (cInfo != null) {
						if (event.getEntity() instanceof LivingEntity) {
							LivingEntity entity = (LivingEntity) event
									.getEntity();
							plugin.getCreatureHandler(
									event.getEntity().getWorld())
									.damageLivingEntity(entity,
											cInfo.getMiscDamage());
							event.setCancelled(true);
						} else {
							System.out.println("Player hit");
							entityProjectileEvent.setDamage(cInfo
									.getMiscDamage());
						}
					}
				}
			}
		}

		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (p.isDead() || (p.getHealth() - event.getDamage()) <= 0) {
				plugin.getCreatureHandler(event.getEntity().getWorld())
						.removePlayer(p);
			}
		}
	}
}
