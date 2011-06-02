package com.elmakers.mine.bukkit.plugins.crowd;

import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

/*
 * Info about creature targeting
 * 
 * @author Andrew Querol(WinSock)
 */

public class TargetInfo {
	
	private Creature creature;
	private CreatureType cType;
	private TargetReason reason;
	private Entity target;
	
	public void setTarget(Entity target) {
		this.target = target;
	}
	
	public Entity getTarget() {
		return target;
	}

	public void setCreature(Creature creature) {
		this.creature = creature;
	}

	public Creature getCreature() {
		return creature;
	}

	public void setcType(CreatureType cType) {
		this.cType = cType;
	}

	public CreatureType getcType() {
		return cType;
	}

	public void setReason(TargetReason reason) {
		this.reason = reason;
	}

	public TargetReason getReason() {
		return reason;
	}
	
}
