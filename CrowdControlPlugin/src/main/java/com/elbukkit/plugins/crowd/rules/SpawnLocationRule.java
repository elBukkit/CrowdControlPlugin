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
    
    private String  elRegionName = "";
    private boolean spawnable    = true;
    
    public SpawnLocationRule(final String name, final CreatureType type, final CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }
    
    @Override
    public boolean check(final Info info) {
        
        if (this.plugin.getRegionsPlugin().getRegionManager(info.getEntity().getWorld()).getRegion(this.elRegionName).contains(info.getLocation())) {
            return this.spawnable;
        }
        
        return !this.spawnable;
    }
    
    public void load(final Configuration config, final String node) {
        this.elRegionName = config.getString(node + ".elRegion", "");
    }
    
    @Override
    public void loadFromString(final String data) {
        final String[] split = data.split(" ");
        this.elRegionName = split[0];
        this.spawnable = Boolean.parseBoolean(split[1]);
    }
    
    public void save(final Configuration config, final String node) {
        config.setProperty(node + ".elRegion", this.elRegionName);
        config.setProperty(node + ".spawnable", this.spawnable);
    }
}
