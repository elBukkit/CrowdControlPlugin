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

    public SpawnTimeRule(String name, CreatureType type, CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }

    @Override
    public boolean check(Info info) {

        if (plugin.getCreatureHandler(info.getLocation().getWorld()).isDay()) {
            // ESCA-JAVA0032:
            switch (spawnTime) {
            case DAY:
                return true;
            case NIGHT:
                return false;
            }
        } else {
            // ESCA-JAVA0032:
            switch (spawnTime) {
            case DAY:
                return true;
            case NIGHT:
                return false;
            }
        }

        return false;
    }

    public void load(Configuration config, String node) {
        this.spawnTime = Time.valueOf(config.getString(node + ".time", "DAY").toUpperCase());
    }

    @Override
    public void loadFromString(String data) {
        this.spawnTime = Time.valueOf(data);
    }

    public void save(Configuration config, String node) {
        config.setProperty(node + ".time", spawnTime.toString());
    }

}
