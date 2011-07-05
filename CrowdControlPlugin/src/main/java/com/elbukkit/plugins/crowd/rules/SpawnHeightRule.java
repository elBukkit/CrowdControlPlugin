package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * A rule that controls spawning based on height.
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class SpawnHeightRule extends Rule {

    private int min, max;

    public SpawnHeightRule(String name, World world, CreatureType type, CrowdControlPlugin plugin) {
        super(name, world, type, plugin);
        this.ruleType = Type.Spawn;
    }

    @Override
    public void loadFromString(String data) {
        String[] split = data.split(",");
        this.max = Integer.parseInt(split[0]);
        this.min = Integer.parseInt(split[1]);
    }

    public boolean spawn(Info info) {
        if (info.getLocation().getBlockY() >= min) {
            if (info.getLocation().getBlockY() <= max) {
                return true;
            }
        }
        return false;
    }

    public void save(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

    public void load(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean check(Info info) {
        // TODO Auto-generated method stub
        return false;
    }

}
