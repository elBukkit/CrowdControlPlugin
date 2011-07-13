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
    private volatile Nature  creatureNatureDay, creatureNatureNight;
    private volatile int     damage;
    private volatile boolean enabled        = false;
    private volatile int     health;
    private volatile double  spawnChance    = 0.7f;
    private volatile int     targetDistance = 24;
    
    public BaseInfo(Configuration config, String node) {
        this.load(config, node);
    }
    
    public BaseInfo(Nature creatureNatureDay, Nature creatureNatureNight, int damage, int health) {
        this.creatureNatureDay = creatureNatureDay;
        this.creatureNatureNight = creatureNatureNight;
        this.damage = damage;
        this.health = health;
    }
    
    // ESCA-JAVA0138:
    public BaseInfo(Nature creatureNatureDay, Nature creatureNatureNight, int damage, int health, int targetDistance, boolean burnDay, double spawnChance) {
        this.creatureNatureDay = creatureNatureDay;
        this.creatureNatureNight = creatureNatureNight;
        this.damage = damage;
        this.health = health;
        this.targetDistance = targetDistance;
        this.burnDay = burnDay;
        this.spawnChance = spawnChance;
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
    public int getDamage() {
        return this.damage;
    }
    
    @ThreadSafe
    public int getHealth() {
        return this.health;
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
    
    public void load(Configuration config, String node) {
        this.enabled = config.getBoolean(node + ".enabled", false);
        this.burnDay = config.getBoolean(node + ".burnDay", false);
        this.damage = config.getInt(node + ".damage", 0);
        this.creatureNatureDay = Nature.valueOf(config.getString(node + ".nature.day", "PASSIVE").toUpperCase());
        this.creatureNatureNight = Nature.valueOf(config.getString(node + ".nature.night", "PASSIVE").toUpperCase());
        this.health = config.getInt(node + ".health", 10);
        this.spawnChance = config.getDouble(".spawnChance", 0.7D);
        this.targetDistance = config.getInt(".targetDistance", 24);
    }
    
    public void save(Configuration config, String node) {
        config.setProperty(node + ".enabled", false);
        config.setProperty(node + ".burnDay", this.burnDay);
        config.setProperty(node + ".damage", this.damage);
        config.setProperty(node + ".nature.day", this.creatureNatureDay.toString());
        config.setProperty(node + ".nature.night", this.creatureNatureNight.toString());
        config.setProperty(node + ".health", this.health);
        config.setProperty(node + ".spawnChance", this.spawnChance);
        config.setProperty(node + ".targetDistance", this.targetDistance);
    }
    
    @ThreadSafe
    public void setBurnDay(boolean burnDay) {
        this.burnDay = burnDay;
    }
    
    @ThreadSafe
    public void setCreatureNatureDay(Nature creatureNature) {
        this.creatureNatureDay = creatureNature;
    }
    
    @ThreadSafe
    public void setCreatureNatureNight(Nature creatureNatureNight) {
        this.creatureNatureNight = creatureNatureNight;
    }
    
    @ThreadSafe
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    @ThreadSafe
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @ThreadSafe
    public void setHealth(int health) {
        this.health = health;
    }
    
    @ThreadSafe
    public void setSpawnChance(float spawnChance) {
        this.spawnChance = spawnChance;
    }
    
    @ThreadSafe
    public void setTargetDistance(int targetDistance) {
        this.targetDistance = targetDistance;
    }
}
