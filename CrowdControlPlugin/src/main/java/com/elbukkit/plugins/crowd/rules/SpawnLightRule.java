package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World;
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

    public SpawnLightRule(String name, World world, CreatureType type, CrowdControlPlugin plugin) {
        super(name, world, type, plugin);
        this.ruleType = Type.Spawn;
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean check(Info info) {
        if (info.getLocation().getBlock().getLightLevel() >= min) {
            if (info.getLocation().getBlock().getLightLevel() <= max) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void loadFromString(String data) {
        String[] split = data.split(",");
        this.max = Integer.parseInt(split[0]);
        this.min = Integer.parseInt(split[1]);
    }

    public void save(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

    public void load(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

}
