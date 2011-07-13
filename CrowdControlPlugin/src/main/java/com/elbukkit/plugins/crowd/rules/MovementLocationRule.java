package com.elbukkit.plugins.crowd.rules;

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
    
    public MovementLocationRule(final String name, final CreatureType type, final CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.MOVEMENT;
    }
    
    @Override
    public boolean check(final Info info) {
        if (this.plugin.getRegionsPlugin().getRegionManager(info.getEntity().getWorld()).getRegion(this.elRegionName).contains(info.getLocation())) {
            return true;
        }
        
        return false;
    }
    
    public void load(final Configuration config, final String node) {
        this.elRegionName = config.getString(node + ".elRegion", "");
    }
    
    @Override
    public void loadFromString(final String data) {
        this.elRegionName = data;
    }
    
    public void save(final Configuration config, final String node) {
        config.setProperty(node + ".elRegion", this.elRegionName);
    }
}
