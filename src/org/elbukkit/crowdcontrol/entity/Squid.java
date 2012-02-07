package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;

public class Squid extends Animal {
    public Squid() {
        this.spawnable.remove(Material.GRASS);
        this.spawnable.add(Material.WATER);
        this.notSpawnable.remove(Material.WATER);
    }
}
