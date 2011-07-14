package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;
import com.elbukkit.plugins.crowd.utils.BukkitEnumUtils;

/**
 * A rule that allows or disallows creatures based on the environment, Normal,
 * Nether, (Soon) Ather
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
        this.spawnableEnvironment = BukkitEnumUtils.findEnvironment(config.getString(node + ".SpawnableEnvironment", "NORMAL"));
    }
    
    @Override
    public void loadFromString(String data) {
        this.spawnableEnvironment = BukkitEnumUtils.findEnvironment(data);
    }
    
    public void save(Configuration config, String node) {
        config.setProperty(node + ".SpawnableEnvironment", this.spawnableEnvironment.toString());
    }
}
