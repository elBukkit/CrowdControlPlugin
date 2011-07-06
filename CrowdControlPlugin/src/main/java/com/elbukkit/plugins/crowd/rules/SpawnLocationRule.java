package com.elbukkit.plugins.crowd.rules;

import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

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

    public SpawnLocationRule(String name, CreatureType type, CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }

    @Override
    public boolean check(Info info) {
        if (plugin.getRegionsPlugin().getRegionManager(info.getEntity().getWorld()).getRegion(elRegionName).contains(info.getLocation())) {
            return true;
        }

        return false;
    }

    public void load(Configuration config, String node) {
        this.elRegionName = config.getString(node + ".elRegion", "");
    }

    @Override
    public void loadFromString(String data) {
        elRegionName = data;
    }

    public void save(Configuration config, String node) {
        config.setProperty(node + ".elRegion", this.elRegionName);
    }
}
