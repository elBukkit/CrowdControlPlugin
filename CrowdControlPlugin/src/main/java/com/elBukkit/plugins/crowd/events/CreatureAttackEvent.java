package com.elBukkit.plugins.crowd.events;

import java.util.EventObject;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;

/*
 * A simple event for creature attacking
 * 
 * @author Andrew Querol(winsock)
 */

public class CreatureAttackEvent extends EventObject implements Cancellable {

    private static final long serialVersionUID = 3745243179503069710L;
    private LivingEntity attacked;
    private LivingEntity attacker;
    private boolean canceled = false;
    private Location location;

    public CreatureAttackEvent(Object sender, Location location, LivingEntity attacker, LivingEntity attacked) {
        super(sender);

        this.location = location;
        this.attacker = attacker;
        this.attacked = attacked;
    }

    public LivingEntity getAttacked() {
        return attacked;
    }

    public LivingEntity getAttacker() {
        return attacker;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }
}
