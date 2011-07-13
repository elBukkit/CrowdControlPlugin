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
    private volatile int          idleTicks = 0;
    private final Location        lastLocation;
    private volatile CreatureType type;
    
    public CrowdCreature(LivingEntity entity, CreatureType type, BaseInfo info) {
        this.entity = entity;
        this.lastLocation = entity.getLocation().clone();
        this.type = type;
        this.baseInfo = info;
        
        if (entity instanceof Slime) {
            entity.setHealth((info.getHealth() * 3) ^ (((Slime) entity).getSize() - 1)); // Geometric series 3^n n = size - 1. Close enough to the default minecraft values
        } else {
            entity.setHealth(info.getHealth());
        }
    }
    
    @ThreadSafe
    public BaseInfo getBaseInfo() {
        return this.baseInfo;
    }
    
    @ThreadSafe
    public Location getCurrentLocation() {
        return this.entity.getLocation().clone();
    }
    
    @ThreadSafe
    public int getDamage() {
        if (this.entity instanceof Slime) {
            return this.baseInfo.getDamage() * (2 * (((Slime) this.entity).getSize() - 1)); // Arithmetic series 2n, n = size -1
        }
        
        return this.baseInfo.getDamage();
    }
    
    @ThreadSafe
    // Thread safe to get but not to use
    public LivingEntity getEntity() {
        return this.entity;
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
    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
    
    @ThreadSafe
    public void setIdleTicks(int idleTicks) {
        this.idleTicks = idleTicks;
    }
    
    public void setLocation(Location loc) {
        this.entity.teleport(loc);
    }
}
