package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * A rule that allows or disallows creatures based on the environment, Normal, Nether, (Soon) Ather
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class SpawnEnvironmentRule extends Rule {

    private Environment spawnableEnvironment;

    public SpawnEnvironmentRule(String name, CreatureType type, CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }

    @Override
    public boolean check(Info info) {
        if (this.spawnableEnvironment.equals(info.getEnv())) {
            return true;
        }
        return false;
    }

    public void load(Configuration config, String node) {
        this.spawnableEnvironment = Environment.valueOf(config.getString(node + ".SpawnableEnvironment", "NORMAL").toUpperCase());
    }

    @Override
    public void loadFromString(String data) {
        this.spawnableEnvironment = Environment.valueOf(data.toUpperCase());
    }

    public void save(Configuration config, String node) {
        config.setProperty(node + ".SpawnableEnvironment", spawnableEnvironment.toString());
    }
}
