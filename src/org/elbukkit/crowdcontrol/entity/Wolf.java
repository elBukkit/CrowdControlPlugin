package org.elbukkit.crowdcontrol.entity;

import org.bukkit.block.Biome;

public class Wolf extends Tameable {
    public Wolf() {
        this.biome.add(Biome.FOREST);
        this.biome.add(Biome.TAIGA);
        this.biome.add(Biome.TAIGA_HILLS);
        this.health = 8;
        this.creatureNatureDay = Nature.NEUTRAL;
        this.creatureNatureNight = Nature.NEUTRAL;
    }
}
