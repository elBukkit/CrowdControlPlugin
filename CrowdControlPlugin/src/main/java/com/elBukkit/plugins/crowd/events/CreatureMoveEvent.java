package com.elBukkit.plugins.crowd.events;

import java.util.EventObject;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;

import com.elBukkit.plugins.crowd.creature.CrowdCreature;

/*
 * A simple event event that fires when a creature moves a whole block.
 * 
 * @author Andrew Querol(winsock)
 */

public class CreatureMoveEvent extends EventObject implements Cancellable {

    private static final long serialVersionUID = -3743383825319521868L;
    private boolean canceled = false;
    private CrowdCreature creature;
    private Location newLocation;
    private Location prevLocation;

    public CreatureMoveEvent(Object sender, Location prevLocation, Location newLocation, CrowdCreature c) {
        super(sender);

        this.prevLocation = prevLocation;
        this.setNewLocation(newLocation);
        this.creature = c;
    }

    public CrowdCreature getCreature() {
        return creature;
    }

    public Location getNewLocation() {
        return newLocation;
    }

    public Location getPrevLocation() {
        return prevLocation;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }

    public void setNewLocation(Location newLocation) {
        this.newLocation = newLocation;
    }

}
