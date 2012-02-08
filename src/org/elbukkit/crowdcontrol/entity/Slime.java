package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;
import org.bukkit.World.Environment;

public class Slime extends LivingEntity {
    
    protected int baseHealthMulti = 4;
    
    public Slime() {
        this.notSpawnable.add(Material.WATER);
        this.notSpawnable.add(Material.STATIONARY_WATER);
        this.environment.add(Environment.NORMAL);
        this.maxSpawnHeight = 40;
    }
    
    public int getHealth(int size) {
        return (int) Math.pow(this.health*baseHealthMulti, size-1);
    }
}
