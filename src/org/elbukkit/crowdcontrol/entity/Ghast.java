package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;

public class Ghast extends Monster {
    public Ghast() {
        this.notSpawnable.remove(Material.AIR);
        this.maxSpawnLight = 15;
        this.environment.add(Environment.NETHER);
        this.health = 10;
    }
    
    @Override
    public boolean canSpawn(Block b) {
        
        if (!super.canSpawn(b)) {
            return false;
        }
        
        for (int y = 0; y < 5; y ++) {
            Block testBlock = b.getWorld().getBlockAt(b.getX(), b.getY() + y, b.getZ());
            
            if (testBlock.getType() != Material.AIR) {
                return false;
            }
        }
        
        return true;
    }
}
