package org.elbukkit.crowdcontrol.entity;

import org.bukkit.World.Environment;

public class Zombie extends Monster {
    public Zombie() {
        this.environment.add(Environment.NORMAL);
        this.burnDay = true;
    }
}
