package com.elbukkit.plugins.crowd.events;

import java.util.EventObject;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;

import com.elbukkit.plugins.crowd.creature.CrowdCreature;

/**
 * A simple event event that fires when a creature moves a whole block.
 * 
 * @author Andrew Querol(winsock)
 * @version 1.0
 */
public class CreatureMoveEvent extends EventObject implements Cancellable {

    private static final long serialVersionUID = -3743383825319521868L;
    private boolean canceled = false;
    private transient CrowdCreature creature;
    private transient Location newLocation;
    private transient Location prevLocation;

    public CreatureMoveEvent(Object sender, Location prevLocation, Location newLocation, CrowdCreature c) {
        super(sender);

        this.prevLocation = prevLocation;
        this.newLocation = newLocation;
        this.creature = c;
    }

    /**
     * Gets the crowd creature that moved
     * 
     * @return {@link CrowdCreature}
     */
    public CrowdCreature getCreature() {
        return creature;
    }

    /**
     * Gets the location the crowd creature wants to move to
     * 
     * @return {@link CrowdCreature}
     */
    public Location getNewLocation() {
        return newLocation;
    }

    /**
     * Gets the location that the crowd creature was previously at.
     * 
     * @return {@link Location}
     */
    public Location getPrevLocation() {
        return prevLocation;
    }

    /**
     * Gets if the event was canceled
     * 
     * @return {@link Boolean}
     */
    public boolean isCancelled() {
        return canceled;
    }

    /**
     * Sets if the event is canceled
     * 
     * @param cancel
     *            {@link Boolean}
     */
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }

    /**
     * Sets a new location to move the crowd creature to
     * 
     * @param newLocation
     *            {@link Location}
     */
    public void setNewLocation(Location newLocation) {
        this.newLocation = newLocation;
    }

}
