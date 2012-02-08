package org.elbukkit.crowdcontrol.entity;

import org.bukkit.World.Environment;

public class LavaSlime extends Slime {
    public LavaSlime() {
        this.maxSpawnLight = 15;
        this.maxSpawnHeight = 128;
        this.environment.add(Environment.NETHER);
    }
}
