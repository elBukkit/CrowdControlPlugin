package com.elBukkit.bukkit.plugins.crowd.creature;

/*
 * Info about a creature
 * 
 * @author Andrew Querol(WinSock)
 */

public class CreatureInfo {

	private Nature creatureNatureDay, creatureNatureNight;
	private int collisionDamage, miscDamage; // Misc damage is for like ghast
												// fireballs, and other damage
	private int health;
	private int targetDistance = 24;
	private boolean burnDay = false;
	private float spawnChance = 0.7f;

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

}
