package org.elbukkit.crowdcontrol.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;

public enum CreatureType {
    CREEPER("Creeper"), SKELETON("Skeleton"), SPIDER("Spider"), GIANT("Giant"), ZOMBIE("Zombie"), SLIME("Slime"), GHAST("Ghast"), PIG_ZOMBIE("PigZombie"), ENDERMAN("Enderman"), CAVE_SPIDER("CaveSpider"), SILVERFISH("Silverfish"), BLAZE("Blaze"), MAGMA_CUBE("LavaSlime"), ENDER_DRAGON("EnderDragon"), PIG("Pig"), SHEEP("Sheep"), COW("Cow"), CHICKEN("Chicken"), SQUID("Squid"), WOLF("Wolf"), MUSHROOM_COW("MushroomCow"), SNOWMAN("SnowMan"), VILLAGER("Villager");

    private String name = "";
    
    private static final Map<String, CreatureType> NAME_MAP = new HashMap<String, CreatureType>();

    static {
        for (CreatureType type : EnumSet.allOf(CreatureType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }
    
    private CreatureType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static CreatureType fromName(String name) {
    	return NAME_MAP.get(name);
    }

    public org.bukkit.entity.CreatureType toBukkitType() {
        return org.bukkit.entity.CreatureType.fromName(name);
    }
    
    public static CreatureType creatureTypeFromEntity(Entity entity) {
        if ( ! (entity instanceof Creature)) {
            return null;
        }

        String name = entity.getClass().getSimpleName();
        name = name.substring(5); // Remove "Craft"

        return CreatureType.fromName(name);
    }
}
