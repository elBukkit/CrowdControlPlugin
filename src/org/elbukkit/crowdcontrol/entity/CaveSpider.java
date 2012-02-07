package org.elbukkit.crowdcontrol.entity;

import org.bukkit.World.Environment;

public class CaveSpider extends Monster {
    public CaveSpider() {
        this.environment.add(Environment.NORMAL);
        this.canNaturalSpawn = false;
    }
}
