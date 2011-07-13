package com.elbukkit.plugins.crowd.rules;

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
    
    public SpawnReplaceRule(final String name, final CreatureType type, final CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }
    
    @Override
    public boolean check(final Info info) {
        info.setType(this.replaceType);
        return true;
    }
    
    public void init(final CreatureType cType) {
        this.replaceType = cType;
    }
    
    public void load(final Configuration config, final String node) {
        this.replaceType = CreatureType.valueOf(config.getString(node + ".replaceType", "MONSTER").toUpperCase());
        
    }
    
    @Override
    public void loadFromString(final String data) {
        this.replaceType = CreatureType.valueOf(data.toUpperCase());
    }
    
    public void save(final Configuration config, final String node) {
        config.setProperty(node + ".replaceType", this.replaceType.toString());
    }
    
}
