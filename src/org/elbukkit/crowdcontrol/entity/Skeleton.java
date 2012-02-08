package org.elbukkit.crowdcontrol.entity;

import org.bukkit.World.Environment;

public class Skeleton extends Monster {
    public Skeleton() {
        this.environment.add(Environment.NORMAL);
        this.burnDay = true;
    }
}
