package com.elbukkit.plugins.crowd.rules;

import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * A rule to control the time of creature spawning
 * 
 * @author Andrew Querol(winsock)
 * @version 1.0
 */
public class SpawnTimeRule extends Rule {
    
    private Time spawnTime;
    
    public SpawnTimeRule(final String name, final CreatureType type, final CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }
    
    @Override
    public boolean check(final Info info) {
        
        if (this.plugin.getCreatureHandler(info.getLocation().getWorld()).isDay()) {
            // ESCA-JAVA0032:
            switch (this.spawnTime) {
                case DAY:
                    return true;
                case NIGHT:
                    return false;
            }
        } else {
            // ESCA-JAVA0032:
            switch (this.spawnTime) {
                case DAY:
                    return true;
                case NIGHT:
                    return false;
            }
        }
        
        return false;
    }
    
    public void load(final Configuration config, final String node) {
        this.spawnTime = Time.valueOf(config.getString(node + ".time", "DAY").toUpperCase());
    }
    
    @Override
    public void loadFromString(final String data) {
        this.spawnTime = Time.valueOf(data);
    }
    
    public void save(final Configuration config, final String node) {
        config.setProperty(node + ".time", this.spawnTime.toString());
    }
    
}
