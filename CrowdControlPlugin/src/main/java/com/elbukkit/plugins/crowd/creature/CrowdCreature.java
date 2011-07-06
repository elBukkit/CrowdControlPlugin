package com.elbukkit.plugins.crowd.creature;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;

import com.elbukkit.plugins.crowd.utils.ThreadSafe;

/**
 * This class represents a creature, when entity equals null its just base info
 * for making crowd creatures
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class CrowdCreature {

    private BaseInfo baseInfo;
    private LivingEntity entity = null;
    private volatile int health;
    private volatile int idleTicks = 0;
    private Location lastLocation;
    private volatile CreatureType type;

    public CrowdCreature(LivingEntity entity, CreatureType type, BaseInfo info) {
        this.entity = entity;
        this.lastLocation = entity.getLocation().clone();
        this.type = type;
        this.baseInfo = info;
        this.setHealth(info.getHealth());
    }

    @ThreadSafe
    public void damage(int amount) {
        this.health -= amount;
        this.entity.damage(0); // Work around to make the entity turn red
    }

    @ThreadSafe
    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public Location getCurrentLocation() {
        return entity.getLocation().clone();
    }

    @ThreadSafe
    // Thread safe to get but not to use
    public LivingEntity getEntity() {
        return entity;
    }

    @ThreadSafe
    public int getHealth() {
        return health;
    }

    @ThreadSafe
    public int getIdleTicks() {
        return idleTicks;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    @ThreadSafe
    public CreatureType getType() {
        return type;
    }

    @ThreadSafe
    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    @ThreadSafe
    public void setHealth(int health) {
        this.health = health;
    }

    @ThreadSafe
    public void setIdleTicks(int idleTicks) {
        this.idleTicks = idleTicks;
    }

    public void setLocation(Location loc) {
        this.entity.teleport(loc);
    }
}
