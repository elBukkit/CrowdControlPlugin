package com.elbukkit.plugins.crowd;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

// ESCA-JAVA0137:
/**
 * Info about creature spawning
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class Info {
    
    private LivingEntity             entity;
    private Environment              env;
    private int                      id;
    private Location                 location;
    private final CrowdControlPlugin plugin;
    private TargetReason             reason;
    private Entity                   target;
    private CreatureType             type;
    
    public Info(CrowdControlPlugin plugin) {
        this.plugin = plugin;
    }
    
    public LivingEntity getEntity() {
        return this.entity;
    }
    
    public Environment getEnv() {
        return this.env;
    }
    
    public int getID() {
        return this.id;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public TargetReason getReason() {
        return this.reason;
    }
    
    public Entity getTarget() {
        return this.target;
    }
    
    public CreatureType getType() {
        return this.type;
    }
    
    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }
    
    public void setEnv(Environment env) {
        this.env = env;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public void setReason(TargetReason reason) {
        this.reason = reason;
    }
    
    public void setTarget(Entity target) {
        this.target = target;
    }
    
    public void setType(CreatureType type) {
        this.type = type;
    }
    
    public void spawn() {
        Random rand = new Random();
        
        this.entity = this.location.getWorld().spawnCreature(this.location, this.type);
        
        // Random slime size
        if (this.entity instanceof Slime) {
            Slime slime = (Slime) this.entity;
            
            slime.setSize(rand.nextInt(4));
        } else if (this.entity instanceof Spider) {
            if (rand.nextFloat() < this.plugin.getSpiderRiderChance()) {
                LivingEntity rider = this.entity.getWorld().spawnCreature(this.entity.getLocation(), CreatureType.SKELETON);
                Spider spider = (Spider) this.entity;
                spider.setPassenger(rider);
            }
        }
    }
}
