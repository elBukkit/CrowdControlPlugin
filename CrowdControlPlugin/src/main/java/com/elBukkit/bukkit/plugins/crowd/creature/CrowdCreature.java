package com.elBukkit.bukkit.plugins.crowd.creature;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;

import com.elBukkit.bukkit.plugins.crowd.ThreadSafe;

/*
 * This class represents a creature, when entity equals null its just base info for making crowd creatures
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdCreature {

	private volatile boolean burnDay = false;
	private volatile int collisionDamage, miscDamage; // Misc damage is for like
														// ghast fireballs, and
														// other damage
	private volatile Nature creatureNatureDay, creatureNatureNight;
	private volatile boolean enabled = false;
	private LivingEntity entity = null;
	private volatile int health;
	private volatile float spawnChance = 0.7f;
	private volatile int targetDistance = 24;
	private volatile CreatureType type;

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

	@ThreadSafe
	public CrowdCreature create(LivingEntity entity) {
		return new CrowdCreature(creatureNatureDay, creatureNatureNight, collisionDamage, miscDamage, health, targetDistance, burnDay, spawnChance, type, enabled, entity);
	}

	@ThreadSafe
	public void damage(int amount) {
		this.health -= amount;
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
	// Thread safe to get but not to use
	public LivingEntity getEntity() {
		return entity;
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
	public CreatureType getType() {
		return type;
	}

	@ThreadSafe
	public boolean isBurnDay() {
		return burnDay;
	}

	@ThreadSafe
	public boolean isEnabled() {
		return enabled;
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

	@ThreadSafe
	public void setType(CreatureType type) {
		this.type = type;
	}

	@ThreadSafe
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
