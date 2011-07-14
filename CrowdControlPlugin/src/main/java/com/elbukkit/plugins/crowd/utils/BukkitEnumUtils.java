package com.elbukkit.plugins.crowd.utils;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;

public class BukkitEnumUtils {
    public static Material findMaterial(String m) throws IllegalArgumentException {
        try {
            // try to parse as int type id.
            int i = Integer.decode(m);
            Material material = Material.getMaterial(i);
            if (material == null) {
                throw new IllegalArgumentException("Unknown material type id: " + m);
            } else {
                return material;
            }
        } catch (NumberFormatException ne) {
            // try as material name
            for (Material material : Material.values()) {
                if (material.name().equals(m.toUpperCase())) {
                    return material;
                } else if (material.name().replaceAll("_", "").equals(m.toUpperCase())) {
                    return material;
                }
            }
            
            throw new IllegalArgumentException("Unknown material name: " + m);
        }
    }
    
    public static CreatureType findCreatureType(String c) throws IllegalArgumentException {
        for (CreatureType type : CreatureType.values()) {
            if (type.name().equals(c.toUpperCase())) {
                return type;
            } else if (type.name().replaceAll("_", "").equals(c.toUpperCase())) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unknown creature type : " + c);
    }
    
    public static Environment findEnvironment(String es) throws IllegalArgumentException {
        for (Environment e : Environment.values()) {
            if (e.name().equals(es.toUpperCase())) {
                return e;
            } else if (e.name().replaceAll("_", "").equals(es.toUpperCase())) {
                return e;
            }
        }
        
        throw new IllegalArgumentException("Unknown environment : " + es);
    }
}
