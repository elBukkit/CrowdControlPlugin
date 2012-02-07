package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;

public class Enderman extends LivingEntity {

    public Enderman() {
        this.environment.add(Environment.NORMAL);
        this.environment.add(Environment.THE_END);
    }
    
    @Override
    public boolean canSpawn(Block b) {
        if (!super.canSpawn(b)) {
            return false;
        }
        
        for (int y = 0; y < 3; y ++) {
            Block testBlock = b.getWorld().getBlockAt(b.getX(), b.getY() + y, b.getZ());
            
            if (testBlock.getType() != Material.AIR) {
                return false;
            }
        }
        
        return true;
    }
}
