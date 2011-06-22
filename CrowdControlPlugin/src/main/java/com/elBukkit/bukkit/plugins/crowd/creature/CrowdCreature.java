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

	private LivingEntity entity = null;
	private volatile CreatureType type;
	private BaseInfo baseInfo;
	private int health;

	public CrowdCreature(LivingEntity entity, CreatureType type, BaseInfo info) {
		this.entity = entity;
		this.type = type;
		this.baseInfo = info;
		this.setHealth(info.getHealth());
	}

	@ThreadSafe
	// Thread safe to get but not to use
	public LivingEntity getEntity() {
		return entity;
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
	public BaseInfo getBaseInfo() {
		return baseInfo;
	}

	@ThreadSafe
	public void setHealth(int health) {
		this.health = health;
	}

	@ThreadSafe
	public int getHealth() {
		return health;
	}

	@ThreadSafe
	public void damage(int amount) {
		this.health -= amount;
	}
}
