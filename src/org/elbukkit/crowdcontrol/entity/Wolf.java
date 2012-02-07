package org.elbukkit.crowdcontrol.entity;

import org.bukkit.block.Biome;

public class Wolf extends Animal {
    public Wolf() {
        this.biome.add(Biome.FOREST);
        this.biome.add(Biome.TAIGA);
        this.biome.add(Biome.TAIGA_HILLS);
    }
}
