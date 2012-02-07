package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;
import org.bukkit.World.Environment;

public abstract class Animal extends LivingEntity {
    public Animal() {
        this.spawnable.add(Material.GRASS);
        this.notSpawnable.add(Material.AIR);
        this.notSpawnable.add(Material.WATER);
        this.notSpawnable.add(Material.STATIONARY_WATER);
        this.minSpawnLight = 7;
        this.environment.add(Environment.NORMAL);
    }
}
