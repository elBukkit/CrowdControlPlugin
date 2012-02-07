package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;
import org.bukkit.World.Environment;

public class Blaze extends Monster {
    public Blaze() {
        this.spawnable.add(Material.NETHER_BRICK);
        this.environment.add(Environment.NETHER);
        this.maxSpawnLight = 12;
    }
}
