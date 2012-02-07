package org.elbukkit.crowdcontrol.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public enum CreatureType {
    CREEPER("Creeper", Creeper.class), SKELETON("Skeleton", Skeleton.class), SPIDER("Spider", Spider.class), GIANT("Giant", Giant.class), ZOMBIE("Zombie", Zombie.class), SLIME("Slime", Slime.class), GHAST("Ghast", Ghast.class), PIG_ZOMBIE("PigZombie", PigZombie.class), ENDERMAN("Enderman", Enderman.class), CAVE_SPIDER("CaveSpider", CaveSpider.class), SILVERFISH("Silverfish", Silverfish.class), BLAZE("Blaze", Blaze.class), MAGMA_CUBE("LavaSlime", LavaSlime.class), ENDER_DRAGON("EnderDragon", EnderDragon.class), PIG("Pig", Pig.class), SHEEP("Sheep", Sheep.class), COW("Cow", Cow.class), CHICKEN("Chicken", Chicken.class), SQUID("Squid", Squid.class), WOLF("Wolf", Wolf.class), MUSHROOM_COW("MushroomCow", MushroomCow.class), SNOWMAN("Snowman", Snowman.class), VILLAGER("Villager", Villager.class);

    private String name = "";
    private Class<? extends EntityData> classz;

    private static final Map<String, CreatureType> NAME_MAP = new HashMap<String, CreatureType>();
    private static final Map<CreatureType, Class<? extends EntityData>> CLASS_MAP = new HashMap<CreatureType, Class<? extends EntityData>>();

    static {
        for (CreatureType type : EnumSet.allOf(CreatureType.class)) {
            NAME_MAP.put(type.name.toUpperCase(), type);
            CLASS_MAP.put(type, type.classz);
        }
    }

    private CreatureType(String name, Class<? extends EntityData> classz) {
        this.name = name;
        this.classz = classz;
    }

    public String getName() {
        return name;
    }

    public static CreatureType fromName(String name) {
        return NAME_MAP.get(name.toUpperCase());
    }

    public org.bukkit.entity.CreatureType toBukkitType() {
        return org.bukkit.entity.CreatureType.fromName(name);
    }

    public static CreatureType creatureTypeFromEntity(Entity entity) {
        if (!(entity instanceof LivingEntity) && !(entity instanceof Player)) {
            return null;
        }

        String name = entity.getClass().getSimpleName();
        name = name.substring(5); // Remove "Craft"

        return CreatureType.fromName(name);
    }
    
    public static Class<? extends EntityData> getClassFromCreatureType(CreatureType type) {
        return CLASS_MAP.get(type);
    }
}
