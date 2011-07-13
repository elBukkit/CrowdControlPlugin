package com.elbukkit.plugins.crowd.rules;

import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * A rule that controls spawning based on light levels.
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class SpawnLightRule extends Rule {
    
    private int min, max;
    
    public SpawnLightRule(final String name, final CreatureType type, final CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public boolean check(final Info info) {
        if (info.getLocation().getBlock().getLightLevel() >= this.min) {
            if (info.getLocation().getBlock().getLightLevel() <= this.max) {
                return true;
            }
        }
        return false;
    }
    
    public void load(final Configuration config, final String node) {
        this.max = config.getInt(node + ".max", 15);
        this.min = config.getInt(node + ".min", 0);
    }
    
    @Override
    public void loadFromString(final String data) {
        final String[] split = data.split(" ,");
        this.max = Integer.parseInt(split[0]);
        this.min = Integer.parseInt(split[1]);
    }
    
    public void save(final Configuration config, final String node) {
        config.setProperty(node + ".min", this.min);
        config.setProperty(node + ".max", this.max);
        
    }
    
}
