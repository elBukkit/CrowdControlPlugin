package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * A rule that allows or disallows creatures based on the environment, Normal,
 * Nether, (Soon) Ather
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class SpawnEnvironmentRule extends Rule {

    private Environment spawnableEnvironment;

    public SpawnEnvironmentRule(String name, World world, CreatureType type, CrowdControlPlugin plugin) {
        super(name, world, type, plugin);
        this.ruleType = Type.Spawn;
    }

    @Override
    public boolean check(Info info) {
        if (this.spawnableEnvironment.equals(info.getEnv())) {
            return true;
        }
        return false;
    }

    @Override
    public void loadFromString(String data) {
        this.spawnableEnvironment = Environment.valueOf(data);
    }

    public void save(Configuration config, String node) {
        config.setProperty(node + "." + name + ".SpawnableEnvironment", spawnableEnvironment.toString());
    }

    public void load(Configuration config, String node) {
        this.spawnableEnvironment = Environment.valueOf(config.getString(node + "." + name + ".SpawnableEnvironment", "NORMAL"));
    }
}
