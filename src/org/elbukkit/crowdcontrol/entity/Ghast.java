package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;
import org.bukkit.World.Environment;

public class Ghast extends Monster {
    public Ghast() {
        this.notSpawnable.remove(Material.AIR);
        this.maxSpawnLight = 15;
        this.environment.add(Environment.NETHER);
    }
}
