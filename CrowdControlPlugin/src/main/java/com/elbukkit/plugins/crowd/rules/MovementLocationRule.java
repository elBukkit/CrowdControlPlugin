package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * Control where entities can be, i.e. entity zones.
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class MovementLocationRule extends Rule {

    String elRegionName = "";

    public MovementLocationRule(String name, World world, CreatureType type, CrowdControlPlugin plugin) {
        super(name, world, type, plugin);
        this.ruleType = Type.Movement;
    }

    @Override
    public boolean check(Info info) {
        if (plugin.getRegionsPlugin().getRegionManager(world).getRegion(elRegionName).contains(info.getLocation())) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public void loadFromString(String data) {
        this.elRegionName = data;
    }

    public void save(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

    public void load(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }
}
