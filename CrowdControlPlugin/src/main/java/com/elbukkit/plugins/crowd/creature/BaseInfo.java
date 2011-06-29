package com.elbukkit.plugins.crowd.creature;

import com.elbukkit.plugins.crowd.ThreadSafe;

/**
 * A simple class to store the base info used to create crowd creatures
 * 
 * @author WinSock
 * @version 1.0
 */
public class BaseInfo {
    private volatile boolean burnDay = false;
    private volatile int collisionDamage, miscDamage;
    private volatile Nature creatureNatureDay, creatureNatureNight;
    private volatile int health;
    private volatile float spawnChance = 0.7f;
    private volatile int targetDistance = 24;

    public BaseInfo(Nature creatureNatureDay, Nature creatureNatureNight, int collisionDamage, int miscDamage, int health) {
        this.creatureNatureDay = creatureNatureDay;
        this.creatureNatureNight = creatureNatureNight;
        this.collisionDamage = collisionDamage;
        this.miscDamage = miscDamage;
        this.health = health;
    }

    // ESCA-JAVA0138:
    public BaseInfo(Nature creatureNatureDay, Nature creatureNatureNight, int collisionDamage, int miscDamage, int health, int targetDistance, boolean burnDay, float spawnChance) {
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
    public float getSpawnChance() {
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
