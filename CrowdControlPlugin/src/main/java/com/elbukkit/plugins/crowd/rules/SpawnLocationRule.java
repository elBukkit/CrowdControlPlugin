package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * A rule that prevents creatures from spawning in certain 3D cubes.
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class SpawnLocationRule extends Rule {

    String elRegionName = "";

    public SpawnLocationRule(World world, CreatureType type, CrowdControlPlugin plugin) {
        super(world, type, plugin);
        this.ruleType = Type.Spawn;
    }

    @Override
    public boolean check(Info info) {
        if (plugin.getRegionsPlugin().getRegionManager(world).getRegion(elRegionName).contains(info.getLocation())) {
            return true;
        }
        
        return false;
    }

    @Override
    public String getData() {

        return elRegionName;
    }

    @Override
    public void init(String data) {
        elRegionName = data;
    }
}
