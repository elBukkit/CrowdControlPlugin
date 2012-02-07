package org.elbukkit.crowdcontrol.entity;

import org.bukkit.World.Environment;

public class PigZombie extends Monster {
    public PigZombie() {
        this.maxSpawnLight = 15;
        this.environment.add(Environment.NETHER);
    }
}
