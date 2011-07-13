package com.elbukkit.plugins.crowd.events;

import java.util.EventObject;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;

import com.elbukkit.plugins.crowd.creature.CrowdCreature;

/**
 * A simple event for creature attacking
 * 
 * @author Andrew Querol(winsock)
 * @version 1.0
 */
public class CreatureAttackEvent extends EventObject implements Cancellable {
    
    private static final long      serialVersionUID = 3745243179503069710L;
    private transient LivingEntity attacked;
    private transient LivingEntity attacker;
    private boolean                canceled         = false;
    private transient Location     location;
    
    public CreatureAttackEvent(Object sender, Location location, LivingEntity attacker, LivingEntity attacked) {
        super(sender);
        
        this.location = location;
        this.attacker = attacker;
        this.attacked = attacked;
    }
    
    /**
     * Gets the entity that was attacked
     * 
     * @return {@link CrowdCreature}
     */
    public LivingEntity getAttacked() {
        return this.attacked;
    }
    
    /**
     * Gets the entity that did the attacking
     * 
     * @return {@link LivingEntity}
     */
    public LivingEntity getAttacker() {
        return this.attacker;
    }
    
    /**
     * Returns the location of the attacked
     * 
     * @return {@link Location}
     */
    public Location getLocation() {
        return this.location;
    }
    
    /**
     * Gets if the event was canceled
     * 
     * @return {@link Boolean}
     */
    public boolean isCancelled() {
        return this.canceled;
    }
    
    /**
     * Sets if the event is canceled
     * 
     * @param cancel
     *            {@link Boolean}
     */
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }
}
