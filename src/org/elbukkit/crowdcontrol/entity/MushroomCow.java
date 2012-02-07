package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;
import org.bukkit.block.Biome;

public class MushroomCow extends Animal {
    public MushroomCow() {
        this.spawnable.remove(Material.GRASS);
        this.spawnable.add(Material.MYCEL);
        this.biome.add(Biome.MUSHROOM_ISLAND);
        this.biome.add(Biome.MUSHROOM_SHORE);
    }
}
