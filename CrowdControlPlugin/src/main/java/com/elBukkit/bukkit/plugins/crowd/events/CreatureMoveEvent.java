package com.elBukkit.bukkit.plugins.crowd.events;

import java.util.EventObject;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;

public class CreatureMoveEvent extends EventObject implements Cancellable {

	private static final long serialVersionUID = -3743383825319521868L;
	private LivingEntity creature;
	private Location newLocation;
	private Location prevLocation;
	private boolean canceled = false;

	public CreatureMoveEvent(Object sender, Location prevLocation, Location newLocation, LivingEntity creature) {
		super(sender);

		this.prevLocation = prevLocation;
		this.setNewLocation(newLocation);
		this.creature = creature;
	}

	public LivingEntity getCreature() {
		return creature;
	}

	public Location getNewLocation() {
		return newLocation;
	}

	public Location getPrevLocation() {
		return prevLocation;
	}

	public void setNewLocation(Location newLocation) {
		this.newLocation = newLocation;
	}

	public boolean isCancelled() {
		return canceled;
	}

	public void setCancelled(boolean cancel) {
		this.canceled = cancel;	
	}

}
