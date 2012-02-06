package org.elbukkit.crowdcontrol.entity;

public enum CreatureType {
    CREEPER("Creeper"), SKELETON("Skeleton"), SPIDER("Spider"), GIANT("Giant"), ZOMBIE("Zombie"), SLIME("Slime"), GHAST("Ghast"), PIG_ZOMBIE("PigZombie"), ENDERMAN("Enderman"), CAVE_SPIDER("CaveSpider"), SILVERFISH("Silverfish"), BLAZE("Blaze"), MAGMA_CUBE("LavaSlime"), ENDER_DRAGON("EnderDragon"), PIG("Pig"), SHEEP("Sheep"), COW("Cow"), CHICKEN("Chicken"), SQUID("Squid"), WOLF("Wolf"), MUSHROOM_COW("MushroomCow"), SNOWMAN("SnowMan"), VILLAGER("Villager");

    private String name = "";

    private CreatureType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public org.bukkit.entity.CreatureType toBukkitType() {
        return org.bukkit.entity.CreatureType.fromName(name);
    }
}
