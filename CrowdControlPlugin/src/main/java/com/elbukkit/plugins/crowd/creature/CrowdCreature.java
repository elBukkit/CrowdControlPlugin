package com.elbukkit.plugins.crowd.creature;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.elbukkit.plugins.crowd.utils.ThreadSafe;

/**
 * This class represents a creature, when entity equals null its just base info
 * for making crowd creatures
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class CrowdCreature {
    
    private BaseInfo              baseInfo;
    private LivingEntity          entity    = null;
    private volatile int          health;
    private volatile int          idleTicks = 0;
    private final Location        lastLocation;
    private volatile CreatureType type;
    
    public CrowdCreature(final LivingEntity entity, final CreatureType type, final BaseInfo info) {
        this.entity = entity;
        this.lastLocation = entity.getLocation().clone();
        this.type = type;
        this.baseInfo = info;
        this.health = info.getHealth();
        
        if (entity instanceof Slime) {
            this.health *= 3 ^ (((Slime) entity).getSize() - 1); // Geometric series 3^n n = size - 1. Close enough to the default minecraft values
        }
    }
    
    @ThreadSafe
    public void damage(final int amount) {
        this.health -= amount;
        this.entity.damage(0); // Work around to make the entity turn red
    }
    
    @ThreadSafe
    public BaseInfo getBaseInfo() {
        return this.baseInfo;
    }
    
    @ThreadSafe
    public int getCollisionDamage() {
        if (this.entity instanceof Slime) {
            return this.baseInfo.getCollisionDamage() * (2 * (((Slime) this.entity).getSize() - 1)); // Arithmetic series 2n, n = size -1
        }
        
        return this.baseInfo.getCollisionDamage();
    }
    
    @ThreadSafe
    public Location getCurrentLocation() {
        return this.entity.getLocation().clone();
    }
    
    @ThreadSafe
    // Thread safe to get but not to use
    public LivingEntity getEntity() {
        return this.entity;
    }
    
    @ThreadSafe
    public int getHealth() {
        return this.health;
    }
    
    @ThreadSafe
    public int getIdleTicks() {
        return this.idleTicks;
    }
    
    public Location getLastLocation() {
        return this.lastLocation;
    }
    
    @ThreadSafe
    public CreatureType getType() {
        return this.type;
    }
    
    @ThreadSafe
    public boolean isDead() {
        return (this.health <= 0) || this.entity.isDead();
    }
    
    @ThreadSafe
    public void setBaseInfo(final BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
    
    @ThreadSafe
    public void setHealth(final int health) {
        this.health = health;
    }
    
    @ThreadSafe
    public void setIdleTicks(final int idleTicks) {
        this.idleTicks = idleTicks;
    }
    
    public void setLocation(final Location loc) {
        this.entity.teleport(loc);
    }
}
