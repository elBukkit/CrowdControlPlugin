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

    public MaxRule(String name, CreatureType type, CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }

    @Override
    public boolean check(Info info) {

        if (plugin.getCreatureHandler(info.getLocation().getWorld()).getCreatureCount(type) < maxMobs) {
            return true;
        }

        return false;
    }

    public void load(Configuration config, String node) {
        this.maxMobs = config.getInt(node + ".max", 0);
    }

    @Override
    public void loadFromString(String data) {
        this.maxMobs = Integer.parseInt(data);
    }

    public void save(Configuration config, String node) {
        config.setProperty(node + ".max", maxMobs);

    }

}
