package com.elBukkit.bukkit.plugins.crowd.creature;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;

/*
 * This class represents a creature, when entity equals null its just base info for making crowd creatures
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdCreature {

	private boolean burnDay = false;
	private int collisionDamage, miscDamage; // Misc damage is for like ghast
												// fireballs, and other damage
	private Nature creatureNatureDay, creatureNatureNight;
	private boolean enabled = false;
	private LivingEntity entity = null;
	private int health;
	private float spawnChance = 0.7f;
	private int targetDistance = 24;
	private CreatureType type;

	public CrowdCreature(Nature creatureNatureDay, Nature creatureNatureNight, int collisionDamage, int miscDamage, int health, CreatureType type) {
		this.creatureNatureDay = creatureNatureDay;
		this.creatureNatureNight = creatureNatureNight;
		this.collisionDamage = collisionDamage;
		this.miscDamage = miscDamage;
		this.health = health;
		this.type = type;
	}

	public CrowdCreature(Nature creatureNatureDay, Nature creatureNatureNight, int collisionDamage, int miscDamage, int health, int targetDistance, boolean burnDay, float spawnChance, CreatureType type, boolean enabled) {
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

	public CrowdCreature(Nature creatureNatureDay, Nature creatureNatureNight, int collisionDamage, int miscDamage, int health, int targetDistance, boolean burnDay, float spawnChance, CreatureType type, boolean enabled, LivingEntity entity) {
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
		this.entity = entity;
	}

	public CrowdCreature create(LivingEntity entity) {
		return new CrowdCreature(creatureNatureDay, creatureNatureNight, collisionDamage, miscDamage, health, targetDistance, burnDay, spawnChance, type, enabled, entity);
	}

	public void damage(int amount) {
		this.health -= amount;
	}

	public int getCollisionDamage() {
		return collisionDamage;
	}

	public Nature getCreatureNatureDay() {
		return creatureNatureDay;
	}

	public Nature getCreatureNatureNight() {
		return creatureNatureNight;
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public int getHealth() {
		return health;
	}

	public int getMiscDamage() {
		return miscDamage;
	}

	public float getSpawnChance() {
		return spawnChance;
	}

	public int getTargetDistance() {
		return targetDistance;
	}

	public CreatureType getType() {
		return type;
	}

	public boolean isBurnDay() {
		return burnDay;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setBurnDay(boolean burnDay) {
		this.burnDay = burnDay;
	}

	public void setCollisionDamage(int collisionDamage) {
		this.collisionDamage = collisionDamage;
	}

	public void setCreatureNatureDay(Nature creatureNature) {
		this.creatureNatureDay = creatureNature;
	}

	public void setCreatureNatureNight(Nature creatureNatureNight) {
		this.creatureNatureNight = creatureNatureNight;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setMiscDamage(int miscDamage) {
		this.miscDamage = miscDamage;
	}

	public void setSpawnChance(float spawnChance) {
		this.spawnChance = spawnChance;
	}

	public void setTargetDistance(int targetDistance) {
		this.targetDistance = targetDistance;
	}

	public void setType(CreatureType type) {
		this.type = type;
	}

	public void updateBaseInfo(CrowdCreature c) {
		this.burnDay = c.burnDay;
		this.collisionDamage = c.collisionDamage;
		this.creatureNatureDay = c.creatureNatureDay;
		this.creatureNatureNight = c.creatureNatureNight;
		this.enabled = c.enabled;
		this.health = c.health;
		this.miscDamage = c.miscDamage;
		this.spawnChance = c.spawnChance;
		this.targetDistance = c.targetDistance;
	}

}
