package org.elbukkit.crowdcontrol.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class EntityData {

    protected List<Material> spawnable = new ArrayList<Material>();
    protected List<Material> notSpawnable = new ArrayList<Material>();
    protected List<Environment> environment = new ArrayList<Environment>();
    protected List<Biome> biome = new ArrayList<Biome>();
    protected List<Biome> noBiome = new ArrayList<Biome>();
    protected int minSpawnLight = 0;
    protected int maxSpawnLight = 15;
    protected boolean burnDay = false;
    protected int maxSpawnHeight = 128;
    protected int minSpawnHeight = 0;
    protected boolean canNaturalSpawn = true;
    protected double spawnChance = 1;
    protected int max = -1;

    public boolean canSpawn(Block b) {

        Block testBlock = b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ());

        if (spawnable.size() > 0) {
            if (!spawnable.contains(testBlock.getType())) {
                return false;
            }
        }

        if (notSpawnable.contains(testBlock.getType())) {
            return false;
        }

        if (environment.size() > 0) {
            if (!environment.contains(b.getWorld().getEnvironment())) {
                return false;
            }
        }

        if (biome.size() > 0) {
            if (!biome.contains(b.getBiome())) {
                return false;
            }
        }

        if (noBiome.contains(b.getBiome())) {
            return false;
        }

        if (b.getLightLevel() < minSpawnLight) {
            return false;
        }

        if (b.getLightLevel() > maxSpawnLight) {
            return false;
        }

        if (b.getY() > maxSpawnHeight) {
            return false;
        }

        if (b.getY() < minSpawnHeight) {
            return false;
        }

        if (!canNaturalSpawn) {
            return false;
        }

        if (new Random().nextDouble() > spawnChance) {
            return false;
        }

        return true;
    }

    public List<Material> getSpawnable() {
        return spawnable;
    }

    public void setSpawnable(List<Material> spawnable) {
        this.spawnable = spawnable;
    }

    public List<Material> getNotSpawnable() {
        return notSpawnable;
    }

    public void setNotSpawnable(List<Material> notSpawnable) {
        this.notSpawnable = notSpawnable;
    }

    public List<Biome> getBiome() {
        return biome;
    }

    public void setBiome(List<Biome> biome) {
        this.biome = biome;
    }

    public List<Biome> getNoBiome() {
        return noBiome;
    }

    public void setNoBiome(List<Biome> noBiome) {
        this.noBiome = noBiome;
    }

    public int getMinSpawnLight() {
        return minSpawnLight;
    }

    public void setMinSpawnLight(int minSpawnLight) {
        this.minSpawnLight = minSpawnLight;
    }

    public int getMaxSpawnLight() {
        return maxSpawnLight;
    }

    public void setMaxSpawnLight(int maxSpawnLight) {
        this.maxSpawnLight = maxSpawnLight;
    }

    public boolean isBurnDay() {
        return burnDay;
    }

    public void setBurnDay(boolean burnDay) {
        this.burnDay = burnDay;
    }

    public int getMaxSpawnHeight() {
        return maxSpawnHeight;
    }

    public void setMaxSpawnHeight(int maxSpawnHeight) {
        this.maxSpawnHeight = maxSpawnHeight;
    }

    public int getMinSpawnHeight() {
        return minSpawnHeight;
    }

    public void setMinSpawnHeight(int minSpawnHeight) {
        this.minSpawnHeight = minSpawnHeight;
    }

    public boolean isCanNaturalSpawn() {
        return canNaturalSpawn;
    }

    public void setCanNaturalSpawn(boolean canNaturalSpawn) {
        this.canNaturalSpawn = canNaturalSpawn;
    }

    public double getSpawnChance() {
        return spawnChance;
    }

    public void setSpawnChance(double spawnChance) {
        this.spawnChance = spawnChance;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
