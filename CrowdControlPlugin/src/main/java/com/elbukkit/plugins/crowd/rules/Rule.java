package com.elbukkit.plugins.crowd.rules;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.WaterMob;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;
import com.elbukkit.plugins.crowd.utils.Saveable;

/**
 * Base rule class, basic methods required for checking
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public abstract class Rule implements Saveable {
    
    protected String             name = "default";
    protected CrowdControlPlugin plugin;
    protected Type               ruleType;
    protected CreatureType       type;
    
    protected Rule(String name, CreatureType type, CrowdControlPlugin plugin) {
        this.name = name;
        this.type = type;
        this.plugin = plugin;
    }
    
    public abstract boolean check(Info info);
    
    public boolean checkCreatureType(CreatureType cType) { // Check if the
                                                           // creature is
                                                           // effected by the
                                                           // rule
        return this.type.equals(cType);
    }
    
    // Classes used for saving data
    public CreatureType getCreatureType() {
        return this.type;
    }
    
    protected CreatureType getCreatureType(Entity entity) {
        if (entity instanceof LivingEntity) {
            if (entity instanceof Creature) {
                // Animals
                if (entity instanceof Animals) {
                    if (entity instanceof Chicken) {
                        return CreatureType.CHICKEN;
                    } else if (entity instanceof Cow) {
                        return CreatureType.COW;
                    } else if (entity instanceof Pig) {
                        return CreatureType.PIG;
                    } else if (entity instanceof Sheep) {
                        return CreatureType.SHEEP;
                    } else if (entity instanceof Wolf) {
                        return CreatureType.WOLF;
                    }
                }
                // Monsters
                else if (entity instanceof Monster) {
                    if (entity instanceof Zombie) {
                        if (entity instanceof PigZombie) {
                            return CreatureType.PIG_ZOMBIE;
                        } else {
                            return CreatureType.ZOMBIE;
                        }
                    } else if (entity instanceof Creeper) {
                        return CreatureType.CREEPER;
                    } else if (entity instanceof Giant) {
                        return CreatureType.GIANT;
                    } else if (entity instanceof Skeleton) {
                        return CreatureType.SKELETON;
                    } else if (entity instanceof Spider) {
                        return CreatureType.SPIDER;
                    } else if (entity instanceof Slime) {
                        return CreatureType.SLIME;
                    }
                }
                // Water Animals
                else if (entity instanceof WaterMob) {
                    if (entity instanceof Squid) {
                        return CreatureType.SQUID;
                    }
                }
            }
            // Flying
            else if (entity instanceof Flying) {
                if (entity instanceof Ghast) {
                    return CreatureType.GHAST;
                }
            } else {
                return CreatureType.MONSTER;
            }
        }
        return null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Type getType() {
        return this.ruleType;
    }
    
    public abstract void loadFromString(String data);
    
}
