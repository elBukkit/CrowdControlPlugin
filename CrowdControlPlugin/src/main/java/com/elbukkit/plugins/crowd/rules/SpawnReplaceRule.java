package com.elbukkit.plugins.crowd.rules;

import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;
import com.elbukkit.plugins.crowd.utils.BukkitEnumUtils;

/**
 * A rule that replaces a creature type with another
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class SpawnReplaceRule extends Rule {
    
    private CreatureType replaceType;
    
    public SpawnReplaceRule(String name, CreatureType type, CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }
    
    @Override
    public boolean check(Info info) {
        info.setType(this.replaceType);
        return true;
    }
    
    public void init(CreatureType cType) {
        this.replaceType = cType;
    }
    
    public void load(Configuration config, String node) {
        this.replaceType = BukkitEnumUtils.findCreatureType(config.getString(node + ".replaceType", "MONSTER"));
        
    }
    
    @Override
    public void loadFromString(String data) {
        this.replaceType = BukkitEnumUtils.findCreatureType(data.toUpperCase());
    }
    
    public void save(Configuration config, String node) {
        config.setProperty(node + ".replaceType", this.replaceType.toString());
    }
    
}
