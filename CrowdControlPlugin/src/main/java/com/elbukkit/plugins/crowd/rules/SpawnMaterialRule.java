package com.elbukkit.plugins.crowd.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;
import com.elbukkit.plugins.crowd.utils.BukkitEnumUtils;

/**
 * A rule that controls spawning based on the material it spawns on.
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class SpawnMaterialRule extends Rule {
    
    private Set<Material> materials;
    private boolean       spawnable;
    
    public SpawnMaterialRule(String name, CreatureType type, CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.SPAWN;
    }
    
    @Override
    public boolean check(Info info) {
        Material blockMaterial = info.getLocation().getWorld().getBlockAt(info.getLocation().getBlockX(), info.getLocation().getBlockY() - 1, info.getLocation().getBlockZ()).getType();
        Material spawnBlockMaterial = info.getLocation().getWorld().getBlockAt(info.getLocation().getBlockX(), info.getLocation().getBlockY(), info.getLocation().getBlockZ()).getType();
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
    
    public void load(Configuration config, String node) {
        this.materials = new HashSet<Material>();
        List<String> data = config.getStringList(node + ".material", new ArrayList<String>());
        
        for (String s : data) {
            this.materials.add(BukkitEnumUtils.findMaterial(s));
        }
        
        this.spawnable = config.getBoolean(node + ".spawnable", false);
    }
    
    @Override
    public void loadFromString(String data) {
        String[] split = data.split(" ");
        String[] materialArray = split[0].split(",");
        
        for (String s : materialArray) {
            this.materials.add(BukkitEnumUtils.findMaterial(s));
        }
        
        this.spawnable = Boolean.valueOf(split[1]);
    }
    
    public void save(Configuration config, String node) {
        Set<String> materialStrings = new HashSet<String>();
        for (Material m : this.materials) {
            materialStrings.add(m.toString());
        }
        config.setProperty(node + ".material", materialStrings);
        config.setProperty(node + ".spawnable", this.spawnable);
    }
    
}
