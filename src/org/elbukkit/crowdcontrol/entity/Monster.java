package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;

public abstract class Monster extends LivingEntity {
    public Monster() {
        this.notSpawnable.add(Material.AIR);
        this.notSpawnable.add(Material.WATER);
        this.notSpawnable.add(Material.STATIONARY_WATER);
        this.maxSpawnLight = 7;
    }
}
