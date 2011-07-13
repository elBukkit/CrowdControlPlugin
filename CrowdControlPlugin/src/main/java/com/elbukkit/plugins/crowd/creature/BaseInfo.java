package com.elbukkit.plugins.crowd.creature;

import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.utils.Saveable;
import com.elbukkit.plugins.crowd.utils.ThreadSafe;

/**
 * A simple class to store the base info used to create crowd creatures
 * 
 * @author WinSock
 * @version 1.0
 */
public class BaseInfo implements Saveable {
    private volatile boolean burnDay        = false;
    private volatile int     collisionDamage, miscDamage;
    private volatile Nature  creatureNatureDay, creatureNatureNight;
    private volatile boolean enabled        = false;
    private volatile int     health;
    private volatile double  spawnChance    = 0.7f;
    private volatile int     targetDistance = 24;
    
    public BaseInfo(final Configuration config, final String node) {
        this.load(config, node);
    }
    
    public BaseInfo(final Nature creatureNatureDay, final Nature creatureNatureNight, final int collisionDamage, final int miscDamage, final int health) {
        this.creatureNatureDay = creatureNatureDay;
        this.creatureNatureNight = creatureNatureNight;
        this.collisionDamage = collisionDamage;
        this.miscDamage = miscDamage;
        this.health = health;
    }
    
    // ESCA-JAVA0138:
    public BaseInfo(final Nature creatureNatureDay, final Nature creatureNatureNight, final int collisionDamage, final int miscDamage, final int health, final int targetDistance, final boolean burnDay, final double spawnChance) {
        this.creatureNatureDay = creatureNatureDay;
        this.creatureNatureNight = creatureNatureNight;
        this.collisionDamage = collisionDamage;
        this.miscDamage = miscDamage;
        this.health = health;
        this.targetDistance = targetDistance;
        this.burnDay = burnDay;
        this.spawnChance = spawnChance;
    }
    
    @ThreadSafe
    public int getCollisionDamage() {
        return this.collisionDamage;
    }
    
    @ThreadSafe
    public Nature getCreatureNatureDay() {
        return this.creatureNatureDay;
    }
    
    @ThreadSafe
    public Nature getCreatureNatureNight() {
        return this.creatureNatureNight;
    }
    
    @ThreadSafe
    public int getHealth() {
        return this.health;
    }
    
    @ThreadSafe
    public int getMiscDamage() {
        return this.miscDamage;
    }
    
    @ThreadSafe
    public double getSpawnChance() {
        return this.spawnChance;
    }
    
    @ThreadSafe
    public int getTargetDistance() {
        return this.targetDistance;
    }
    
    @ThreadSafe
    public boolean isBurnDay() {
        return this.burnDay;
    }
    
    @ThreadSafe
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void load(final Configuration config, final String node) {
        this.enabled = config.getBoolean(node + ".enabled", false);
        this.burnDay = config.getBoolean(node + ".burnDay", false);
        this.collisionDamage = config.getInt(node + ".damage.collision", 0);
        this.miscDamage = config.getInt(node + ".damage.misc", 0);
        this.creatureNatureDay = Nature.valueOf(config.getString(node + ".nature.day", "PASSIVE").toUpperCase());
        this.creatureNatureNight = Nature.valueOf(config.getString(node + ".nature.night", "PASSIVE").toUpperCase());
        this.health = config.getInt(node + ".health", 10);
        this.spawnChance = config.getDouble(".spawnChance", 0.7D);
        this.targetDistance = config.getInt(".targetDistance", 24);
    }
    
    public void save(final Configuration config, final String node) {
        config.setProperty(node + ".enabled", false);
        config.setProperty(node + ".burnDay", this.burnDay);
        config.setProperty(node + ".damage.collision", this.collisionDamage);
        config.setProperty(node + ".damage.misc", this.miscDamage);
        config.setProperty(node + ".nature.day", this.creatureNatureDay.toString());
        config.setProperty(node + ".nature.night", this.creatureNatureNight.toString());
        config.setProperty(node + ".health", this.health);
        config.setProperty(node + ".spawnChance", this.spawnChance);
        config.setProperty(node + ".targetDistance", this.targetDistance);
    }
    
    @ThreadSafe
    public void setBurnDay(final boolean burnDay) {
        this.burnDay = burnDay;
    }
    
    @ThreadSafe
    public void setCollisionDamage(final int collisionDamage) {
        this.collisionDamage = collisionDamage;
    }
    
    @ThreadSafe
    public void setCreatureNatureDay(final Nature creatureNature) {
        this.creatureNatureDay = creatureNature;
    }
    
    @ThreadSafe
    public void setCreatureNatureNight(final Nature creatureNatureNight) {
        this.creatureNatureNight = creatureNatureNight;
    }
    
    @ThreadSafe
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    @ThreadSafe
    public void setHealth(final int health) {
        this.health = health;
    }
    
    @ThreadSafe
    public void setMiscDamage(final int miscDamage) {
        this.miscDamage = miscDamage;
    }
    
    @ThreadSafe
    public void setSpawnChance(final float spawnChance) {
        this.spawnChance = spawnChance;
    }
    
    @ThreadSafe
    public void setTargetDistance(final int targetDistance) {
        this.targetDistance = targetDistance;
    }
}
