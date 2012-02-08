package org.elbukkit.crowdcontrol.entity;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Giant extends Monster {
    public Giant() {
        this.canNaturalSpawn = false;
    }
    
    @Override
    public boolean canSpawn(Block b) {
        
        if (!super.canSpawn(b)) {
            return false;
        }
        
        for (int y = 0; y < 10; y ++) {
            Block testBlock = b.getWorld().getBlockAt(b.getX(), b.getY() + y, b.getZ());
            
            if (testBlock.getType() != Material.AIR) {
                return false;
            }
        }
        
        return true;
    }
}
