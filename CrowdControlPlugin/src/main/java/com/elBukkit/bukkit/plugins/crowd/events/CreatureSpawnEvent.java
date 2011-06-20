package com.elBukkit.bukkit.plugins.crowd.events;

import java.util.EventObject;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.Cancellable;

import com.elBukkit.bukkit.plugins.crowd.creature.CrowdCreature;

public class CreatureSpawnEvent extends EventObject implements Cancellable{
	private static final long serialVersionUID = -5031301408889128018L;
	private boolean canceled = false;
	private CrowdCreature info;
	private Location location;
	private CreatureType type;

	public CreatureSpawnEvent(Object sender, Location location, CreatureType type, CrowdCreature info) {
		super(sender);

		this.setLocation(location);
		this.setType(type);
		this.setInfo(info);
	}

	public CrowdCreature getInfo() {
		return info;
	}

	public Location getLocation() {
		return location;
	}

	public CreatureType getType() {
		return type;
	}

	public boolean isCancelled() {
		return canceled;
	}

	public void setCancelled(boolean cancel) {
		this.canceled = cancel;
	}

	public void setInfo(CrowdCreature info) {
		this.info = info;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setType(CreatureType type) {
		this.type = type;
	}
}
