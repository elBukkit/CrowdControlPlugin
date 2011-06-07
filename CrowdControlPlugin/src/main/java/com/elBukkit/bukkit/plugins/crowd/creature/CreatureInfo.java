package com.elBukkit.bukkit.plugins.crowd.creature;

/*
 * Info about a creature
 * 
 * @author Andrew Querol(WinSock)
 */

public class CreatureInfo {

	private Nature creatureNature;
	private int collisionDamage, miscDamage; // Misc damage is for like ghast
												// fireballs, and other damage
	private int health;

	public void setCreatureNature(Nature creatureNature) {
		this.creatureNature = creatureNature;
	}

	public Nature getCreatureNature() {
		return creatureNature;
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

}
