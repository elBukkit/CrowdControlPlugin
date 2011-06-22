package com.elBukkit.bukkit.plugins.crowd.creature;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.elBukkit.bukkit.plugins.crowd.CrowdControlPlugin;

/*
 * This handles all of the damage stuff for my plugin
 * 
 * @author Andrew Querol(winsock)
 */

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

				CrowdCreature crowdCreature = plugin.getCreatureHandler(p.getWorld()).getCrowdCreature(e);

				if (crowdCreature != null) {
					LivingEntity entity = e;

					if (entity instanceof Creature) { // Living entities
														// cannot have
														// targets?
						Creature c = (Creature) entity;
						// Targeting System
						if (distance < crowdCreature.getBaseInfo().getTargetDistance()) {
							if (plugin.getCreatureHandler(p.getWorld()).isDay()) {
								switch (crowdCreature.getBaseInfo().getCreatureNatureDay()) {
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
								switch (crowdCreature.getBaseInfo().getCreatureNatureNight()) {
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
							switch (crowdCreature.getBaseInfo().getCreatureNatureDay()) {
							case Aggressive:
								p.damage(crowdCreature.getBaseInfo().getCollisionDamage());
								break;
							case Neutral:
								Set<Player> attackingPlayers = plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(entity);
								if (attackingPlayers != null && attackingPlayers.size() > 0) {
									if (attackingPlayers.contains(p)) {
										p.damage(crowdCreature.getBaseInfo().getCollisionDamage(), entity);
									}
								}
								break;
							}
						} else {
							switch (crowdCreature.getBaseInfo().getCreatureNatureNight()) {
							case Aggressive:
								p.damage(crowdCreature.getBaseInfo().getCollisionDamage());
								break;
							case Neutral:
								Set<Player> attackingPlayers = plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(entity);
								if (attackingPlayers != null && attackingPlayers.size() > 0) {
									if (attackingPlayers.contains(p)) {
										p.damage(crowdCreature.getBaseInfo().getCollisionDamage(), entity);
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
					CrowdCreature crowdCreature = plugin.getCreatureHandler(e.getWorld()).getCrowdCreature((LivingEntity) e);
					if (plugin.getCreatureHandler(e.getWorld()).shouldBurn(e.getLocation()) && crowdCreature.getBaseInfo().isBurnDay()) {
						e.setFireTicks(15);
					} else {
						e.setFireTicks(0);
					}
				}
			}
		}
	}
}
