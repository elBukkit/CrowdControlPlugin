package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;
import org.bukkit.World.Environment;

public class EnderDragon extends Monster {
    public EnderDragon() {
        this.notSpawnable.remove(Material.AIR);
        this.environment.add(Environment.THE_END);
    }
}
