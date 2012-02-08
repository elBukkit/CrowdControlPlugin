package org.elbukkit.crowdcontrol.entity;

import org.bukkit.entity.Creature;

public class EntityInstance {

    private int curentHealth;
    private EntityData data;
    private org.bukkit.entity.LivingEntity entity;
    private Nature curentNature;

    public EntityInstance(EntityData data, org.bukkit.entity.LivingEntity entity) {
        this.data = data;
        this.entity = entity;

        if (entity.getLocation().getBlock().getLightFromSky() > 7) {
            this.curentNature = data.creatureNatureDay;
        } else {
            this.curentNature = data.creatureNatureNight;
        }

        if ((data instanceof Tameable) && (entity instanceof org.bukkit.entity.Tameable)) {
            org.bukkit.entity.Tameable tammed = (org.bukkit.entity.Tameable) entity;
            if (tammed.isTamed()) {
                Tameable tData = (Tameable) data;
                this.curentHealth = tData.tammedHealth;
            } else {
                this.curentHealth = data.getHealth();
            }
        } else if ((data instanceof Slime) && (entity instanceof org.bukkit.entity.Slime)) {
            org.bukkit.entity.Slime slime = (org.bukkit.entity.Slime) entity;
            this.curentHealth = ((Slime) data).getHealth(slime.getSize());
        } else {
            this.curentHealth = data.getHealth();
        }
    }

    public boolean isDead() {
        return curentHealth <= 0;
    }

    public void damage(int ammount) {
        curentHealth -= ammount;
    }

    public void damage(int ammount, org.bukkit.entity.LivingEntity e) {

        if (curentNature == Nature.NEUTRAL) {
            curentNature = Nature.AGGRESSIVE;
            if (entity instanceof Creature) {
                Creature c = (Creature) entity;
                c.setTarget(e);
            }
        } else if (curentNature == Nature.AGGRESSIVE) {
            if (entity instanceof Creature) {
                Creature c = (Creature) entity;
                c.setTarget(e);
            }
        }

        curentHealth -= ammount;
    }

    public EntityData getDefaultData() {
        return data;
    }

    public void setHealth(int health) {
        this.curentHealth = health;
    }

    public org.bukkit.entity.LivingEntity getEntity() {
        return entity;
    }

    public Nature getCurentNature() {
        return curentNature;
    }

    public void setNature(Nature nature) {
        this.curentNature = nature;
    }
}
