package org.elbukkit.crowdcontrol.entity;

import org.bukkit.World.Environment;

public class Spider extends Monster {
    public Spider() {
        this.environment.add(Environment.NORMAL);
        this.health = 16;
        this.creatureNatureDay = Nature.NEUTRAL;
        this.creatureNatureNight = Nature.AGGRESSIVE;
    }
}
