package com.elBukkit.bukkit.plugins.crowd.events;

import java.util.EventObject;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.Cancellable;

/*
 * A simple event for creature spawning generated by my custom system
 * 
 * @author Andrew Querol(winsock)
 */

public class CreatureSpawnEvent extends EventObject implements Cancellable {
	private static final long serialVersionUID = -5031301408889128018L;
	private boolean canceled = false;
	private Location location;
	private CreatureType type;

	public CreatureSpawnEvent(Object sender, Location location, CreatureType type) {
		super(sender);

		this.location = location;
		this.type = type;
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
}
