package com.elbukkit.plugins.crowd.creature;

import java.util.Iterator;

import org.bukkit.Location;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;
import com.elbukkit.plugins.crowd.events.CreatureMoveEvent;
import com.elbukkit.plugins.crowd.events.CrowdListener;
import com.elbukkit.plugins.crowd.rules.Type;

/**
 * This handler is what detects creature movements
 * 
 * @author Andrew Querol(winsock)
 * @version 1.0
 */
public class MovementHandler implements Runnable {
    
    private final CreatureHandler handler;
    
    CrowdControlPlugin            plugin;
    
    public MovementHandler(CrowdControlPlugin plugin, CreatureHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }
    
    public void run() {
        Iterator<CrowdCreature> i = this.handler.getCrowdCreatures().iterator();
        
        while (i.hasNext()) {
            CrowdCreature c = i.next();
            
            Location lLoc = c.getLastLocation();
            Location cLoc = c.getCurrentLocation();
            
            if ((cLoc.getBlockX() != lLoc.getBlockX()) || (cLoc.getBlockY() != lLoc.getBlockY()) || (cLoc.getBlockZ() != lLoc.getBlockZ()) || (cLoc.getWorld() != lLoc.getWorld())) {
                Info info = new Info(this.plugin);
                info.setLocation(cLoc);
                info.setEntity(c.getEntity());
                info.setEnv(c.getEntity().getWorld().getEnvironment());
                info.setType(c.getType());
                
                if (this.plugin.getRuleHandler(c.getEntity().getWorld()).passesRules(info, Type.MOVEMENT)) {
                    
                    CreatureMoveEvent event = new CreatureMoveEvent(this, lLoc, cLoc, c);
                    for (CrowdListener cListener : this.plugin.getListeners()) {
                        cListener.onCreatureMove(event);
                    }
                    
                    if (event.isCancelled()) {
                        c.getEntity().teleport(lLoc);
                    } else {
                        if (event.getNewLocation() != cLoc) {
                            c.setLocation(event.getNewLocation());
                        }
                        c.setIdleTicks(0);
                    }
                } else {
                    c.getEntity().teleport(lLoc);
                }
            } else {
                c.setIdleTicks(c.getIdleTicks() + 1);
            }
        }
        
    }
    
}
