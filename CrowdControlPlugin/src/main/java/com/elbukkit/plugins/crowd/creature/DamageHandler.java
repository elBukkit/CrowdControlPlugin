package com.elbukkit.plugins.crowd.creature;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;

/**
 * This handles all of the damage stuff for my plugin
 * 
 * @author Andrew Querol(winsock)
 * @version 1.0
 */
public class DamageHandler implements Runnable {

    private CreatureHandler handler;
    private CrowdControlPlugin plugin;

    public DamageHandler(CrowdControlPlugin plugin, CreatureHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    public void run() {

        for (Player p : handler.getWorld().getPlayers()) {

            Iterator<CrowdCreature> i = handler.getCrowdCreatures().iterator();

            while (i.hasNext()) {

                CrowdCreature crowdCreature = i.next();
                LivingEntity entity = crowdCreature.getEntity();

                double deltax = Math.abs(entity.getLocation().getX() - p.getLocation().getX());
                double deltay = Math.abs(entity.getLocation().getY() - p.getLocation().getY());
                double deltaz = Math.abs(entity.getLocation().getZ() - p.getLocation().getZ());
                double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));

                // Living entities cannot have targets?
                if (entity instanceof Creature) { 
                    Creature c = (Creature) entity;
                    // Targeting System
                    if (distance < crowdCreature.getBaseInfo().getTargetDistance()) {
                        if (plugin.getCreatureHandler(p.getWorld()).isDay()) {
                            switch (crowdCreature.getBaseInfo().getCreatureNatureDay()) {
                            case AGGRESSIVE:
                                c.setTarget(p);
                                break;
                            case NEUTRAL:
                                Set<Player> attackingPlayers = plugin.getCreatureHandler(c.getWorld()).getAttackingPlayers(crowdCreature);
                                if (attackingPlayers != null && attackingPlayers.size() > 0) {
                                    if (attackingPlayers.contains(p)) {
                                        c.setTarget(p);
                                    }
                                }
                                break;
                            }
                        } else {
                            switch (crowdCreature.getBaseInfo().getCreatureNatureNight()) {
                            case AGGRESSIVE:
                                c.setTarget(p);
                                break;
                            case NEUTRAL:
                                Set<Player> attackingPlayers = plugin.getCreatureHandler(c.getWorld()).getAttackingPlayers(crowdCreature);
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
                        case AGGRESSIVE:
                            p.damage(crowdCreature.getBaseInfo().getCollisionDamage());
                            break;
                        case NEUTRAL:
                            Set<Player> attackingPlayers = plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(crowdCreature);
                            if (attackingPlayers != null && attackingPlayers.size() > 0) {
                                if (attackingPlayers.contains(p)) {
                                    p.damage(crowdCreature.getBaseInfo().getCollisionDamage(), entity);
                                }
                            }
                            break;
                        }
                    } else {
                        switch (crowdCreature.getBaseInfo().getCreatureNatureNight()) {
                        case AGGRESSIVE:
                            p.damage(crowdCreature.getBaseInfo().getCollisionDamage());
                            break;
                        case NEUTRAL:
                            Set<Player> attackingPlayers = plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(crowdCreature);
                            if (attackingPlayers != null && attackingPlayers.size() > 0) {
                                if (attackingPlayers.contains(p)) {
                                    p.damage(crowdCreature.getBaseInfo().getCollisionDamage(), entity);
                                }
                            }
                            break;
                        }
                    }
                }

                if (handler.shouldBurn(crowdCreature.getEntity().getLocation()) && crowdCreature.getBaseInfo().isBurnDay()) {
                    crowdCreature.getEntity().setFireTicks(30);
                } else {
                    crowdCreature.getEntity().setFireTicks(0);
                }
            }
        }
    }
}
