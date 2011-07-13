package com.elbukkit.plugins.crowd.rules;

import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * Set maximum number of a creature type
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class MaxRule extends Rule {
    
    private int maxMobs;
    
    public MaxRule(final String name, final CreatureType type, final CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }
    
    @Override
    public boolean check(final Info info) {
        
        if (this.plugin.getCreatureHandler(info.getLocation().getWorld()).getCreatureCount(this.type) < this.maxMobs) {
            return true;
        }
        
        return false;
    }
    
    public void load(final Configuration config, final String node) {
        this.maxMobs = config.getInt(node + ".max", 0);
    }
    
    @Override
    public void loadFromString(final String data) {
        this.maxMobs = Integer.parseInt(data);
    }
    
    public void save(final Configuration config, final String node) {
        config.setProperty(node + ".max", this.maxMobs);
        
    }
    
}
