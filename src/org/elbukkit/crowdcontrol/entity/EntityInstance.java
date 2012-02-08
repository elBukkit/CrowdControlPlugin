package org.elbukkit.crowdcontrol.entity;

public class EntityInstance {
    
    private int curentHealth;
    private EntityData data;
    private org.bukkit.entity.LivingEntity entity;
    
    public EntityInstance(EntityData data, org.bukkit.entity.LivingEntity entity) {
        this.data = data;
        this.entity = entity;
        
        if (data instanceof Tameable && entity instanceof org.bukkit.entity.Tameable) {
            org.bukkit.entity.Tameable tammed = (org.bukkit.entity.Tameable) entity;
            if(tammed.isTamed()) {
                Tameable tData = (Tameable)data;
                this.curentHealth = tData.tammedHealth;
            } else {
                this.curentHealth = data.getHealth();
            }
        } else if (data instanceof Slime && entity instanceof org.bukkit.entity.Slime) {
            org.bukkit.entity.Slime slime = (org.bukkit.entity.Slime)entity;
            this.curentHealth = ((Slime)data).getHealth(slime.getSize());
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
    
    public EntityData getDefaultData() {
        return data;
    }
    
    public void setHealth(int health) {
        this.curentHealth = health;
    }
    
    public org.bukkit.entity.LivingEntity getEntity() {
        return entity;
    }
}
