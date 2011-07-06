package com.elbukkit.plugins.crowd.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private Set<Material> materials;
    private boolean spawnable;

    public SpawnMaterialRule(String name, CreatureType type, CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }

    @Override
    public boolean check(Info info) {
        Material blockMaterial = info.getLocation().getWorld().getBlockAt(info.getLocation().getBlockX(), info.getLocation().getBlockY() - 1, info.getLocation().getBlockZ()).getType();
        Material spawnBlockMaterial = info.getLocation().getWorld().getBlockAt(info.getLocation().getBlockX(), info.getLocation().getBlockY(), info.getLocation().getBlockZ()).getType();
        if (spawnable) {
            if (spawnBlockMaterial == Material.WATER || spawnBlockMaterial == Material.STATIONARY_WATER || spawnBlockMaterial == Material.LAVA || spawnBlockMaterial == Material.STATIONARY_LAVA) {
                if (materials.contains(spawnBlockMaterial)) {
                    return true;
                }
                return false;
            }
            if (materials.contains(blockMaterial)) {
                return true;
            }
            return false;
        } else {
            if (spawnBlockMaterial == Material.WATER || spawnBlockMaterial == Material.STATIONARY_WATER || spawnBlockMaterial == Material.LAVA || spawnBlockMaterial == Material.STATIONARY_LAVA) {
                if (!materials.contains(spawnBlockMaterial)) {
                    return true;
                }
                return false;
            }
            if (!materials.contains(blockMaterial)) {
                return true;
            }
            return false;
        }
    }

    public void load(Configuration config, String node) {
        this.materials = new HashSet<Material>();
        List<Object> data = config.getList(node + ".material");

        for (Object material : data) {
            materials.add(Material.valueOf((String) material));
        }

        this.spawnable = config.getBoolean(node + ".spawnable", false);
    }

    @Override
    public void loadFromString(String data) {
        String[] split = data.split(" ");
        String[] materialArray = split[0].split(",");
        for (String s : materialArray) {
            materials.add(Material.valueOf(s.toUpperCase()));
        }
        spawnable = Boolean.valueOf(split[1]);
    }

    public void save(Configuration config, String node) {
        Set<String> materialStrings = new HashSet<String>();
        for (Material m : materials) {
            materialStrings.add(m.toString());
        }
        config.setProperty(node + ".material", materialStrings);
        config.setProperty(node + ".spawnable", spawnable);
    }

}
