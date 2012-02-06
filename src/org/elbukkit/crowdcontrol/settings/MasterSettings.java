package org.elbukkit.crowdcontrol.settings;

public class MasterSettings {
    private int numberOfTrys = 64;
    private int maxPerChunk = 4;
    private int maxPerWorld = -1;
    private int despawnRadius = 20;
    private double despawnChance = 0.1;
    private int chunkRadius = 4;

    public int getNumberOfTrys() {
        return numberOfTrys;
    }

    public void setNumberOfTrys(int numberOfTrys) {
        this.numberOfTrys = numberOfTrys;
    }

    public int getMaxPerChunk() {
        return maxPerChunk;
    }

    public void setMaxPerChunk(int maxPerChunk) {
        this.maxPerChunk = maxPerChunk;
    }

    public int getMaxPerWorld() {
        return maxPerWorld;
    }

    public void setMaxPerWorld(int maxPerWorld) {
        this.maxPerWorld = maxPerWorld;
    }

    public int getDespawnRadius() {
        return despawnRadius;
    }

    public void setDespawnRadius(int despawnRadius) {
        this.despawnRadius = despawnRadius;
    }

    public double getDespawnChance() {
        return despawnChance;
    }

    public void setDespawnChance(double despawnChance) {
        this.despawnChance = despawnChance;
    }

    public int getChunkRadius() {
        return chunkRadius;
    }

    public void setChunkRadius(int chunkRadius) {
        this.chunkRadius = chunkRadius;
    }
}
