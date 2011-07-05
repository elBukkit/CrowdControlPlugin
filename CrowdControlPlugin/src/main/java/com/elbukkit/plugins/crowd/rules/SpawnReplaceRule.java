package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * A rule that replaces a creature type with another
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class SpawnReplaceRule extends Rule {

    private CreatureType replaceType;

    public SpawnReplaceRule(String name, World world, CreatureType type, CrowdControlPlugin plugin) {
        super(name, world, type, plugin);
        this.ruleType = Type.Spawn;
    }

    @Override
    public boolean check(Info info) {
        info.setType(replaceType);
        return true;
    }

    public void init(CreatureType cType) {
        replaceType = cType;
    }

    @Override
    public void loadFromString(String data) {
        replaceType = CreatureType.valueOf(data);
    }

    public void save(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

    public void load(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

}
