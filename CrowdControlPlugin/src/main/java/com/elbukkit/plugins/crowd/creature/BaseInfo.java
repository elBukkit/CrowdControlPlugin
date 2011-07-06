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
    private volatile boolean burnDay = false;
    private volatile int collisionDamage, miscDamage;
    private volatile Nature creatureNatureDay, creatureNatureNight;
    private volatile boolean enabled = false;
    private volatile int health;
    private volatile double spawnChance = 0.7f;
    private volatile int targetDistance = 24;

    public BaseInfo(Configuration config, String node) {
        this.load(config, node);
    }

    public BaseInfo(Nature creatureNatureDay, Nature creatureNatureNight, int collisionDamage, int miscDamage, int health) {
        this.creatureNatureDay = creatureNatureDay;
        this.creatureNatureNight = creatureNatureNight;
        this.collisionDamage = collisionDamage;
        this.miscDamage = miscDamage;
        this.health = health;
    }

    // ESCA-JAVA0138:
    public BaseInfo(Nature creatureNatureDay, Nature creatureNatureNight, int collisionDamage, int miscDamage, int health, int targetDistance, boolean burnDay, double spawnChance) {
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
        return collisionDamage;
    }

    @ThreadSafe
    public Nature getCreatureNatureDay() {
        return creatureNatureDay;
    }

    @ThreadSafe
    public Nature getCreatureNatureNight() {
        return creatureNatureNight;
    }

    @ThreadSafe
    public int getHealth() {
        return health;
    }

    @ThreadSafe
    public int getMiscDamage() {
        return miscDamage;
    }

    @ThreadSafe
    public double getSpawnChance() {
        return spawnChance;
    }

    @ThreadSafe
    public int getTargetDistance() {
        return targetDistance;
    }

    @ThreadSafe
    public boolean isBurnDay() {
        return burnDay;
    }

    @ThreadSafe
    public boolean isEnabled() {
        return enabled;
    }

    public void load(Configuration config, String node) {
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

    public void save(Configuration config, String node) {
        config.setProperty(node + ".enabled", false);
        config.setProperty(node + ".burnDay", burnDay);
        config.setProperty(node + ".damage.collision", collisionDamage);
        config.setProperty(node + ".damage.misc", miscDamage);
        config.setProperty(node + ".nature.day", creatureNatureDay.toString());
        config.setProperty(node + ".nature.night", creatureNatureNight.toString());
        config.setProperty(node + ".health", health);
        config.setProperty(node + ".spawnChance", spawnChance);
        config.setProperty(node + ".targetDistance", targetDistance);
    }

    @ThreadSafe
    public void setBurnDay(boolean burnDay) {
        this.burnDay = burnDay;
    }

    @ThreadSafe
    public void setCollisionDamage(int collisionDamage) {
        this.collisionDamage = collisionDamage;
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
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @ThreadSafe
    public void setHealth(int health) {
        this.health = health;
    }

    @ThreadSafe
    public void setMiscDamage(int miscDamage) {
        this.miscDamage = miscDamage;
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
