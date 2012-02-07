package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;

public abstract class LivingEntity extends EntityData {
    public LivingEntity() {
        this.notSpawnable.add(Material.LAVA);
        this.notSpawnable.add(Material.STATIONARY_LAVA);
    }
}
