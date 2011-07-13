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
    
    private final CreatureHandler    handler;
    private final CrowdControlPlugin plugin;
    
    public DamageHandler(final CrowdControlPlugin plugin, final CreatureHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }
    
    public void run() {
        
        final Iterator<CrowdCreature> i = this.handler.getCrowdCreatures().iterator();
        
        while (i.hasNext()) {
            
            final CrowdCreature crowdCreature = i.next();
            final LivingEntity entity = crowdCreature.getEntity();
            final List<Entity> near = entity.getNearbyEntities(crowdCreature.getBaseInfo().getTargetDistance(), crowdCreature.getBaseInfo().getTargetDistance(), crowdCreature.getBaseInfo().getTargetDistance());
            Entry<Double, Player> player = null;
            
            if (crowdCreature.getType() == CreatureType.WOLF) {
                final Wolf wolf = (Wolf) entity;
                
                // Ignore tamed wolves
                if (wolf.isTamed()) {
                    continue;
                }
            }
            
            if ((near == null) || (near.size() <= 0)) {
                continue;
            }
            
            for (final Entity e : near) {
                
                if (e instanceof Player) {
                    
                    final double deltax = Math.abs(entity.getLocation().getX() - e.getLocation().getX());
                    final double deltay = Math.abs(entity.getLocation().getY() - e.getLocation().getY());
                    final double deltaz = Math.abs(entity.getLocation().getZ() - e.getLocation().getZ());
                    final double distance = (deltax * deltax) + (deltay * deltay) + (deltaz * deltaz);
                    
                    if (player != null) {
                        if (player.getKey() > distance) {
                            player = new AbstractMap.SimpleEntry<Double, Player>(distance, (Player) e);
                        }
                    } else {
                        player = new AbstractMap.SimpleEntry<Double, Player>(distance, (Player) e);
                    }
                }
                
            }
            
            if (player == null) {
                continue;
            }
            
            final double distance = Math.sqrt(player.getKey());
            final Player p = player.getValue();
            
            // Living entities cannot have targets?
            if (entity instanceof Creature) {
                final Creature c = (Creature) entity;
                
                final Info info = new Info(this.plugin);
                info.setEntity(entity);
                info.setTarget(p);
                info.setReason(TargetReason.CLOSEST_PLAYER);
                
                // Targeting System
                if (distance < crowdCreature.getBaseInfo().getTargetDistance()) {
                    if (this.plugin.getCreatureHandler(p.getWorld()).isDay()) {
                        switch (crowdCreature.getBaseInfo().getCreatureNatureDay()) {
                            case AGGRESSIVE:
                                if (this.plugin.getRuleHandler(this.handler.getWorld()).passesRules(info, Type.TARGET)) {
                                    c.setTarget(p);
                                }
                                break;
                            case NEUTRAL:
                                final Set<Player> attackingPlayers = this.plugin.getCreatureHandler(c.getWorld()).getAttackingPlayers(crowdCreature);
                                if ((attackingPlayers != null) && (attackingPlayers.size() > 0)) {
                                    if (attackingPlayers.contains(p)) {
                                        if (this.plugin.getRuleHandler(this.handler.getWorld()).passesRules(info, Type.TARGET)) {
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
                                if (this.plugin.getRuleHandler(this.handler.getWorld()).passesRules(info, Type.TARGET)) {
                                    c.setTarget(p);
                                }
                                break;
                            case NEUTRAL:
                                final Set<Player> attackingPlayers = this.plugin.getCreatureHandler(c.getWorld()).getAttackingPlayers(crowdCreature);
                                if ((attackingPlayers != null) && (attackingPlayers.size() > 0)) {
                                    if (attackingPlayers.contains(p)) {
                                        if (this.plugin.getRuleHandler(this.handler.getWorld()).passesRules(info, Type.TARGET)) {
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
                    
                    if (this.plugin.getCreatureHandler(p.getWorld()).isDay()) {
                        switch (crowdCreature.getBaseInfo().getCreatureNatureDay()) {
                            case AGGRESSIVE:
                                p.damage(crowdCreature.getCollisionDamage());
                                break;
                            case NEUTRAL:
                                final Set<Player> attackingPlayers = this.plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(crowdCreature);
                                if ((attackingPlayers != null) && (attackingPlayers.size() > 0)) {
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
                                final Set<Player> attackingPlayers = this.plugin.getCreatureHandler(p.getWorld()).getAttackingPlayers(crowdCreature);
                                if ((attackingPlayers != null) && (attackingPlayers.size() > 0)) {
                                    if (attackingPlayers.contains(p)) {
                                        p.damage(crowdCreature.getCollisionDamage(), entity);
                                    }
                                }
                                break;
                        }
                    }
                }
            }
            
            if (this.handler.shouldBurn(crowdCreature.getEntity().getLocation()) && crowdCreature.getBaseInfo().isBurnDay()) {
                crowdCreature.getEntity().setFireTicks(30);
            } else {
                crowdCreature.getEntity().setFireTicks(0);
            }
        }
    }
}
