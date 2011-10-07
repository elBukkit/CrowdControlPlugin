package com.elbukkit.plugins.crowd.creature;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;
import com.elbukkit.plugins.crowd.rules.Type;
import com.elbukkit.plugins.crowd.utils.ClassUtils;

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
            LivingEntity creature = crowdCreature.getEntity();
            Player targetPlayer = null;
            {
                Location creatureLocation = creature.getLocation();
                double creatureX = creatureLocation.getX(), creatureY = creatureLocation.getY(), creatureZ = creatureLocation.getZ(), maxTargetDistance = crowdCreature.getBaseInfo().getTargetDistance();
	            double targetDistance = -1.0D;
	            for (Player player : this.handler.getWorld().getPlayers()) {
	                if (player.isDead()) {
	                    continue;
	                }
	                Location playerLocation = player.getLocation();
	                double playerDistance = (playerLocation.getX() - creatureX) * (playerLocation.getX() - creatureX) + (playerLocation.getY() - creatureY) * (playerLocation.getY() - creatureY) + (playerLocation.getZ() - creatureZ) * (playerLocation.getZ() - creatureZ);
	                if ((maxTargetDistance < 0.0D || playerDistance < maxTargetDistance * maxTargetDistance) && (targetDistance == -1.0D || playerDistance < targetDistance)) {
	                    targetDistance = playerDistance;
	                    targetPlayer = player;
	                }
	            }
            }
            
            if (crowdCreature.getType() == CreatureType.WOLF) {
                Wolf wolf = (Wolf) creature;
                
                // Ignore tamed wolves
                if (wolf.isTamed()) {
                    continue;
                }
            }
            
            if (targetPlayer == null) {
                continue;
            }
            
            // Living entities cannot have targets?                
            Info info = new Info(this.plugin);
            info.setEntity(creature);
            info.setTarget(targetPlayer);
            info.setReason(TargetReason.CLOSEST_PLAYER);
            
            // Targeting System
            if (creature instanceof Creature) {
                
                Creature c = (Creature) creature;
                
                if (this.plugin.getCreatureHandler(targetPlayer.getWorld()).isDay()) {
                    switch (crowdCreature.getBaseInfo().getCreatureNatureDay()) {
                        case AGGRESSIVE:
                            if (this.plugin.getRuleHandler(this.handler.getWorld()).passesRules(info, Type.TARGET)) {
                                c.setTarget(targetPlayer);
                            }
                            break;
                        case NEUTRAL:
                            Set<Player> attackingPlayers = this.plugin.getCreatureHandler(c.getWorld()).getAttackingPlayers(crowdCreature);
                            if ((attackingPlayers != null) && (attackingPlayers.size() > 0)) {
                                if (attackingPlayers.contains(targetPlayer)) {
                                    if (this.plugin.getRuleHandler(this.handler.getWorld()).passesRules(info, Type.TARGET)) {
                                        c.setTarget(targetPlayer);
                                    }
                                    c.setTarget(targetPlayer);
                                }
                            }
                            break;
                    }
                } else {
                    switch (crowdCreature.getBaseInfo().getCreatureNatureNight()) {
                        case AGGRESSIVE:
                            if (this.plugin.getRuleHandler(this.handler.getWorld()).passesRules(info, Type.TARGET)) {
                                c.setTarget(targetPlayer);
                            }
                            break;
                        case NEUTRAL:
                            Set<Player> attackingPlayers = this.plugin.getCreatureHandler(c.getWorld()).getAttackingPlayers(crowdCreature);
                            if ((attackingPlayers != null) && (attackingPlayers.size() > 0)) {
                                if (attackingPlayers.contains(targetPlayer)) {
                                    if (this.plugin.getRuleHandler(this.handler.getWorld()).passesRules(info, Type.TARGET)) {
                                        c.setTarget(targetPlayer);
                                    }
                                }
                            }
                            break;
                    }
                }
            } else if (creature instanceof Ghast) {
                Ghast ghast = (Ghast) creature;
                try {
                    ClassUtils.setPrivateField(ghast, "target", targetPlayer);
                } catch (Exception ex) {
                    plugin.getLog().info("[CrowdControl] Error setting ghast target!");
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
