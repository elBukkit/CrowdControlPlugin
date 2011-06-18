package com.elBukkit.bukkit.plugins.crowd.creature;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.elBukkit.bukkit.plugins.crowd.CrowdControlPlugin;

public class DamageHandler implements Runnable {

	private CrowdControlPlugin plugin;

	public DamageHandler(CrowdControlPlugin plugin) {
		this.plugin = plugin;
	}

	public void run() {

		for (Player p : plugin.getServer().getOnlinePlayers()) {
			for (LivingEntity e : p.getWorld().getLivingEntities()) {
				double deltax = Math.abs(e.getLocation().getX() - p.getLocation().getX());
				double deltay = Math.abs(e.getLocation().getY() - p.getLocation().getY());
				double deltaz = Math.abs(e.getLocation().getZ() - p.getLocation().getZ());
				double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));

				CreatureInfo cInfo = plugin.getCreatureHandler(p.getWorld()).getInfo(plugin.getCreatureHandler(p.getWorld()).getCreatureType(e));

				if (cInfo != null) {
					LivingEntity entity = e;

					if (entity instanceof Creature) { // Living entities
														// cannot have
														// targets?
						Creature c = (Creature) entity;
						// Targeting System
						if (distance < cInfo.getTargetDistance()) {
							if (plugin.getCreatureHandler(p.getWorld()).isDay()) {
								switch (cInfo.getCreatureNatureDay()) {
								case Aggressive:
									c.setTarget(p);
									break;
								case Neutral:
									Set<Player> attackingPlayers = plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(entity);
									if (attackingPlayers != null && attackingPlayers.size() > 0) {
										if (attackingPlayers.contains(p)) {
											c.setTarget(p);
										}
									}
									break;
								}
							} else {
								switch (cInfo.getCreatureNatureNight()) {
								case Aggressive:
									c.setTarget(p);
									break;
								case Neutral:
									Set<Player> attackingPlayers = plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(entity);
									if (attackingPlayers != null && attackingPlayers.size() > 0) {
										if (attackingPlayers.contains(p)) {
											c.setTarget(p);
										}
									}
									break;
								}
							}
						}
					}

					// Collision damage system
					if (distance <= 1.8) {

						if (plugin.getCreatureHandler(p.getWorld()).isDay()) {
							switch (cInfo.getCreatureNatureDay()) {
							case Aggressive:
								p.damage(cInfo.getCollisionDamage());
								break;
							case Neutral:
								Set<Player> attackingPlayers = plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(entity);
								if (attackingPlayers != null && attackingPlayers.size() > 0) {
									if (attackingPlayers.contains(p)) {
										p.damage(cInfo.getCollisionDamage(), entity);
									}
								}
								break;
							}
						} else {
							switch (cInfo.getCreatureNatureNight()) {
							case Aggressive:
								p.damage(cInfo.getCollisionDamage());
								break;
							case Neutral:
								Set<Player> attackingPlayers = plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(entity);
								if (attackingPlayers != null && attackingPlayers.size() > 0) {
									if (attackingPlayers.contains(p)) {
										p.damage(cInfo.getCollisionDamage(), entity);
									}
								}
								break;
							}
						}
					}
				}
			}
		}

		// This controls the mob burning
		for (World w : Bukkit.getServer().getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (e instanceof LivingEntity) {
					CreatureInfo cInfo = plugin.getCreatureHandler(e.getWorld()).getInfo(plugin.getCreatureHandler(e.getWorld()).getCreatureType((LivingEntity) e));
					if (plugin.getCreatureHandler(e.getWorld()).shouldBurn(e.getLocation()) && cInfo.isBurnDay()) {
						e.setFireTicks(15);
					} else {
						e.setFireTicks(0);
					}
				}
			}
		}
	}
}
