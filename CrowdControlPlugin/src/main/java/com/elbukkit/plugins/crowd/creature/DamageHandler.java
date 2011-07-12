package com.elbukkit.plugins.crowd.creature;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;
import com.elbukkit.plugins.crowd.rules.Type;

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
        

            Iterator<CrowdCreature> i = handler.getCrowdCreatures().iterator();

            while (i.hasNext()) {

                CrowdCreature crowdCreature = i.next();
                LivingEntity entity = crowdCreature.getEntity();
                List<Entity> near = entity.getNearbyEntities(crowdCreature.getBaseInfo().getTargetDistance(), crowdCreature.getBaseInfo().getTargetDistance(), crowdCreature.getBaseInfo().getTargetDistance());
                Entry<Double, Player> player = null;
                
                if (crowdCreature.getType() == CreatureType.WOLF) {
                    Wolf wolf = (Wolf) entity;

                    // Ignore tamed wolves
                    if (wolf.isTamed()) {
                        continue;
                    }
                }
                
                if (near == null || near.size() <= 0) {
                    continue;
                }
                
                for (Entity e : near) {
                    
                    if (e instanceof Player) {
                        
                        double deltax = Math.abs(entity.getLocation().getX() - e.getLocation().getX());
                        double deltay = Math.abs(entity.getLocation().getY() - e.getLocation().getY());
                        double deltaz = Math.abs(entity.getLocation().getZ() - e.getLocation().getZ());
                        double distance = (deltax * deltax) + (deltay * deltay) + (deltaz * deltaz);
                        
                        if (player != null) {
                            if (player.getKey() > distance) {
                                player = new AbstractMap.SimpleEntry<Double, Player>(distance, (Player)e);
                            }
                        } else {
                            player = new AbstractMap.SimpleEntry<Double, Player>(distance, (Player)e);
                        }
                    }
                    
                }
                
                if (player == null) {
                    continue;
                }
                
                double distance = Math.sqrt(player.getKey());
                Player p = player.getValue();

                // Living entities cannot have targets?
                if (entity instanceof Creature) {
                    Creature c = (Creature) entity;

                    Info info = new Info(plugin);
                    info.setEntity(entity);
                    info.setTarget(p);
                    info.setReason(TargetReason.CLOSEST_PLAYER);

                    // Targeting System
                    if (distance < crowdCreature.getBaseInfo().getTargetDistance()) {
                        if (plugin.getCreatureHandler(p.getWorld()).isDay()) {
                            switch (crowdCreature.getBaseInfo().getCreatureNatureDay()) {
                            case AGGRESSIVE:
                                if (plugin.getRuleHandler(handler.getWorld()).passesRules(info, Type.TARGET)) {
                                    c.setTarget(p);
                                }
                                break;
                            case NEUTRAL:
                                Set<Player> attackingPlayers = plugin.getCreatureHandler(c.getWorld()).getAttackingPlayers(crowdCreature);
                                if (attackingPlayers != null && attackingPlayers.size() > 0) {
                                    if (attackingPlayers.contains(p)) {
                                        if (plugin.getRuleHandler(handler.getWorld()).passesRules(info, Type.TARGET)) {
                                            c.setTarget(p);
                                        }
                                        c.setTarget(p);
                                    }
                                }
                                break;
                            }
                        } else {
                            switch (crowdCreature.getBaseInfo().getCreatureNatureNight()) {
                            case AGGRESSIVE:
                                if (plugin.getRuleHandler(handler.getWorld()).passesRules(info, Type.TARGET)) {
                                    c.setTarget(p);
                                }
                                break;
                            case NEUTRAL:
                                Set<Player> attackingPlayers = plugin.getCreatureHandler(c.getWorld()).getAttackingPlayers(crowdCreature);
                                if (attackingPlayers != null && attackingPlayers.size() > 0) {
                                    if (attackingPlayers.contains(p)) {
                                        if (plugin.getRuleHandler(handler.getWorld()).passesRules(info, Type.TARGET)) {
                                            c.setTarget(p);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }

                // Collision damage system
                if (distance <= 1.8) {

                    if (p.getNoDamageTicks() > 0) {

                        if (plugin.getCreatureHandler(p.getWorld()).isDay()) {
                            switch (crowdCreature.getBaseInfo().getCreatureNatureDay()) {
                            case AGGRESSIVE:
                                p.damage(crowdCreature.getCollisionDamage());
                                break;
                            case NEUTRAL:
                                Set<Player> attackingPlayers = plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(crowdCreature);
                                if (attackingPlayers != null && attackingPlayers.size() > 0) {
                                    if (attackingPlayers.contains(p)) {
                                        p.damage(crowdCreature.getCollisionDamage(), entity);
                                    }
                                }
                                break;
                            }
                        } else {
                            switch (crowdCreature.getBaseInfo().getCreatureNatureNight()) {
                            case AGGRESSIVE:
                                p.damage(crowdCreature.getCollisionDamage());
                                break;
                            case NEUTRAL:
                                Set<Player> attackingPlayers = plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(crowdCreature);
                                if (attackingPlayers != null && attackingPlayers.size() > 0) {
                                    if (attackingPlayers.contains(p)) {
                                        p.damage(crowdCreature.getCollisionDamage(), entity);
                                    }
                                }
                                break;
                            }
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
