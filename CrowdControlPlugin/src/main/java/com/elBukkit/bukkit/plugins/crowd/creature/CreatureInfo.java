package com.elBukkit.bukkit.plugins.crowd.creature;

import org.bukkit.entity.CreatureType;

/*
 * Info about a creature
 * 
 * @author Andrew Querol(WinSock)
 */

public class CreatureInfo {

    private Nature creatureNatureDay,
            creatureNatureNight;
    private int collisionDamage,
            miscDamage; // Misc damage is for like ghast
                        // fireballs, and other damage
    private int health;
    private int targetDistance = 24;
    private boolean burnDay = false;
    private float spawnChance = 0.7f;
    private CreatureType type;
    private boolean enabled = false;

    public CreatureInfo(Nature creatureNatureDay, Nature creatureNatureNight, int collisionDamage, int miscDamage, int health, int targetDistance, boolean burnDay, float spawnChance, CreatureType type, boolean enabled) {
        this.creatureNatureDay = creatureNatureDay;
        this.creatureNatureNight = creatureNatureNight;
        this.collisionDamage = collisionDamage;
        this.miscDamage = miscDamage;
        this.health = health;
        this.targetDistance = targetDistance;
        this.burnDay = burnDay;
        this.spawnChance = spawnChance;
        this.type = type;
        this.enabled = enabled;
    }

    public CreatureInfo(Nature creatureNatureDay, Nature creatureNatureNight, int collisionDamage, int miscDamage, int health, CreatureType type) {
        this.creatureNatureDay = creatureNatureDay;
        this.creatureNatureNight = creatureNatureNight;
        this.collisionDamage = collisionDamage;
        this.miscDamage = miscDamage;
        this.health = health;
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCreatureNatureDay(Nature creatureNature) {
        this.creatureNatureDay = creatureNature;
    }

    public Nature getCreatureNatureDay() {
        return creatureNatureDay;
    }

    public void setCollisionDamage(int collisionDamage) {
        this.collisionDamage = collisionDamage;
    }

    public int getCollisionDamage() {
        return collisionDamage;
    }

    public void setMiscDamage(int miscDamage) {
        this.miscDamage = miscDamage;
    }

    public int getMiscDamage() {
        return miscDamage;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setSpawnChance(float spawnChance) {
        this.spawnChance = spawnChance;
    }

    public float getSpawnChance() {
        return spawnChance;
    }

    public void setCreatureNatureNight(Nature creatureNatureNight) {
        this.creatureNatureNight = creatureNatureNight;
    }

    public Nature getCreatureNatureNight() {
        return creatureNatureNight;
    }

    public void setBurnDay(boolean burnDay) {
        this.burnDay = burnDay;
    }

    public boolean isBurnDay() {
        return burnDay;
    }

    public void setTargetDistance(int targetDistance) {
        this.targetDistance = targetDistance;
    }

    public int getTargetDistance() {
        return targetDistance;
    }

    public CreatureType getType() {
        return type;
    }

    public void setType(CreatureType type) {
        this.type = type;
    }

    public CreatureInfo copy() {
        return new CreatureInfo(creatureNatureDay, creatureNatureNight, collisionDamage, miscDamage, health, targetDistance, burnDay, spawnChance, type, enabled);
    }

}
