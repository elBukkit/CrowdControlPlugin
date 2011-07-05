package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World;
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

    public MaxRule(String name, World world, CreatureType type, CrowdControlPlugin plugin) {
        super(name, world, type, plugin);
        this.ruleType = Type.Spawn;
    }

    @Override
    public boolean check(Info info) {

        if (plugin.getCreatureHandler(info.getLocation().getWorld()).getCreatureCount(type) < maxMobs) {
            return true;
        }

        return false;
    }

    @Override
    public void loadFromString(String data) {
        this.maxMobs = Integer.parseInt(data);
    }

    public void save(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

    public void load(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

}
