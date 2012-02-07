package org.elbukkit.crowdcontrol.settings;

public class MasterSettings {
    private int numberOfTrys = 64;
    private int maxPerChunk = 4;
    private int maxPerWorld = -1;
    private int noSpawnRadius = 10;
    private int chunkRadius = 7;

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

    public int getChunkRadius() {
        return chunkRadius;
    }

    public void setChunkRadius(int chunkRadius) {
        this.chunkRadius = chunkRadius;
    }

    public int getNoSpawnRadius() {
        return noSpawnRadius;
    }

    public void setNoSpawnRadius(int noSpawnRadius) {
        this.noSpawnRadius = noSpawnRadius;
    }
}
