package org.elbukkit.crowdcontrol.entity;

import org.bukkit.World.Environment;

public class LavaSlime extends Monster {
    public LavaSlime() {
        this.maxSpawnLight = 15;
        this.environment.add(Environment.NETHER);
    }
}
