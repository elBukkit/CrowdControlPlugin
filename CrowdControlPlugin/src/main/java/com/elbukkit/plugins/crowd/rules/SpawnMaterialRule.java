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
    private boolean       spawnable;
    
    public SpawnMaterialRule(final String name, final CreatureType type, final CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }
    
    @Override
    public boolean check(final Info info) {
        final Material blockMaterial = info.getLocation().getWorld().getBlockAt(info.getLocation().getBlockX(), info.getLocation().getBlockY() - 1, info.getLocation().getBlockZ()).getType();
        final Material spawnBlockMaterial = info.getLocation().getWorld().getBlockAt(info.getLocation().getBlockX(), info.getLocation().getBlockY(), info.getLocation().getBlockZ()).getType();
        if (this.spawnable) {
            if ((spawnBlockMaterial == Material.WATER) || (spawnBlockMaterial == Material.STATIONARY_WATER) || (spawnBlockMaterial == Material.LAVA) || (spawnBlockMaterial == Material.STATIONARY_LAVA)) {
                if (this.materials.contains(spawnBlockMaterial)) {
                    return true;
                }
                return false;
            }
            if (this.materials.contains(blockMaterial)) {
                return true;
            }
            return false;
        } else {
            if ((spawnBlockMaterial == Material.WATER) || (spawnBlockMaterial == Material.STATIONARY_WATER) || (spawnBlockMaterial == Material.LAVA) || (spawnBlockMaterial == Material.STATIONARY_LAVA)) {
                if (!this.materials.contains(spawnBlockMaterial)) {
                    return true;
                }
                return false;
            }
            if (!this.materials.contains(blockMaterial)) {
                return true;
            }
            return false;
        }
    }
    
    public void load(final Configuration config, final String node) {
        this.materials = new HashSet<Material>();
        final List<Object> data = config.getList(node + ".material");
        
        for (final Object material : data) {
            this.materials.add(Material.valueOf((String) material));
        }
        
        this.spawnable = config.getBoolean(node + ".spawnable", false);
    }
    
    @Override
    public void loadFromString(final String data) {
        final String[] split = data.split(" ");
        final String[] materialArray = split[0].split(",");
        for (final String s : materialArray) {
            this.materials.add(Material.valueOf(s.toUpperCase()));
        }
        this.spawnable = Boolean.valueOf(split[1]);
    }
    
    public void save(final Configuration config, final String node) {
        final Set<String> materialStrings = new HashSet<String>();
        for (final Material m : this.materials) {
            materialStrings.add(m.toString());
        }
        config.setProperty(node + ".material", materialStrings);
        config.setProperty(node + ".spawnable", this.spawnable);
    }
    
}
