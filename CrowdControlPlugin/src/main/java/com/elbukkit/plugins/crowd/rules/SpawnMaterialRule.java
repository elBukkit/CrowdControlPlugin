package com.elbukkit.plugins.crowd.rules;

import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * A rule that controls spawning based on the material it spawns on.
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class SpawnMaterialRule extends Rule {

    private Material material;

    public SpawnMaterialRule(String name, CreatureType type, CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }

    @Override
    public boolean check(Info info) {
        Material blockMaterial = info.getEntity().getWorld().getBlockAt(info.getLocation().getBlockX(), info.getLocation().getBlockY() - 1, info.getLocation().getBlockZ()).getType();
        Material spawnBlockMaterial = info.getEntity().getWorld().getBlockAt(info.getLocation().getBlockX(), info.getLocation().getBlockY(), info.getLocation().getBlockZ()).getType();
        if (material != blockMaterial && material != spawnBlockMaterial) {
            return true;
        }
        return false;
    }

    public void load(Configuration config, String node) {
        this.material = Material.valueOf(config.getString(node + ".material", "AIR").toUpperCase());
    }

    @Override
    public void loadFromString(String data) {
        String[] split = data.split(",");
        material = Material.valueOf(split[0].toUpperCase());
    }

    public void save(Configuration config, String node) {
        config.setProperty(node + ".material", material.toString());
    }

}
