package com.elbukkit.plugins.crowd.creature;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.server.EntityHuman;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
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
    
    public DamageHandler(CrowdControlPlugin plugin, CreatureHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }
    
    public void run() {
        
        Iterator<CrowdCreature> i = this.handler.getCrowdCreatures().iterator();
        
        while (i.hasNext()) {
            
            CrowdCreature crowdCreature = i.next();
            LivingEntity entity = crowdCreature.getEntity();
            EntityHuman human = ((CraftWorld)handler.getWorld()).getHandle().findNearbyPlayer(((CraftEntity)entity).getHandle(), crowdCreature.getBaseInfo().getTargetDistance());
            
            if (crowdCreature.getType() == CreatureType.WOLF) {
                Wolf wolf = (Wolf) entity;
                
                // Ignore tamed wolves
                if (wolf.isTamed()) {
                    continue;
                }
            }
            
            if (human == null) {
                continue;
            }
            
            CraftEntity e = CraftEntity.getEntity((CraftServer)Bukkit.getServer(), human);
            
            if (!(e instanceof CraftPlayer)) {
                continue;
            }
            
            Player p = (CraftPlayer)e;
            
            double deltax = Math.abs(entity.getLocation().getX() - p.getLocation().getX());
            double deltay = Math.abs(entity.getLocation().getY() - p.getLocation().getY());
            double deltaz = Math.abs(entity.getLocation().getZ() - p.getLocation().getZ());
            double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));

            // Living entities cannot have targets?
            if (entity instanceof Creature) {
                Creature c = (Creature) entity;
                
                Info info = new Info(this.plugin);
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
                                Set<Player> attackingPlayers = this.plugin.getCreatureHandler(c.getWorld()).getAttackingPlayers(crowdCreature);
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
                                Set<Player> attackingPlayers = this.plugin.getCreatureHandler(c.getWorld()).getAttackingPlayers(crowdCreature);
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
            
            if (this.handler.shouldBurn(crowdCreature.getEntity().getLocation()) && crowdCreature.getBaseInfo().isBurnDay()) {
                crowdCreature.getEntity().setFireTicks(30);
            } else {
                crowdCreature.getEntity().setFireTicks(0);
            }
        }
    }
}
