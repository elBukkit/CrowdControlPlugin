package org.elbukkit.crowdcontrol.entity;

import org.bukkit.World.Environment;

public class Slime extends Monster {
    public Slime() {
        this.environment.add(Environment.NORMAL);
        this.maxSpawnHeight = 40;
    }
}
