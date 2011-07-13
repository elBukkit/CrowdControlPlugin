package com.elbukkit.plugins.crowd.creature;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.WaterMob;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.utils.FileUtils;
import com.elbukkit.plugins.crowd.utils.ThreadSafe;

/**
 * Handles everything to do with a crowd creature.
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class CreatureHandler implements Runnable {
    
    private final Map<CrowdCreature, Set<Player>> attacked;
    private final Map<CreatureType, BaseInfo>     baseInfo;
    private final Configuration                   config;
    private final File                            configFile;
    private final Set<CrowdCreature>              crowdCreatureSet;
    private final Set<CreatureType>               enabledCreatures;
    private final CrowdControlPlugin              plugin;
    private final Random                          rand = new Random();
    private final World                           world;
    
    public CreatureHandler(World w, CrowdControlPlugin plugin) throws IOException {
        this.world = w;
        this.plugin = plugin;
        this.baseInfo = new ConcurrentHashMap<CreatureType, BaseInfo>();
        this.crowdCreatureSet = Collections.newSetFromMap(new ConcurrentHashMap<CrowdCreature, Boolean>());
        this.enabledCreatures = Collections.newSetFromMap(new ConcurrentHashMap<CreatureType, Boolean>());
        this.attacked = new ConcurrentHashMap<CrowdCreature, Set<Player>>();
        
        this.configFile = new File(plugin.getDataFolder() + File.separator + this.world.getName() + ".yml");
        if (!this.configFile.exists()) {
            File defaults = new File(plugin.getDataFolder() + File.separator + this.world.getEnvironment().toString() + ".yml");
            if (defaults.exists()) {
                FileUtils.copyFile(defaults, this.configFile);
            } else {
                if (this.configFile.createNewFile()) {
                    plugin.getLog().info("[CrowdControl] Created config for " + w.getName() + "!");
                }
            }
        }
        this.config = new Configuration(this.configFile);
        this.config.load();
        
        List<String> mobs = this.config.getKeys("mobs");
        if (mobs != null) {
            for (String mob : mobs) {
                BaseInfo info = new BaseInfo(this.config, "mobs." + mob);
                this.baseInfo.put(CreatureType.valueOf(mob.toUpperCase()), info);
                
                if (info.isEnabled()) {
                    this.enabledCreatures.add(CreatureType.valueOf(mob.toUpperCase()));
                }
            }
        } else {
            this.generateDefaults();
        }
        
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new SpawnHandler(plugin, this), 0, 10);
        
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new MovementHandler(plugin, this), 0, 15);
        
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new DamageHandler(plugin, this), 0, 20);
    }
    
    @ThreadSafe
    public void addAttacked(CrowdCreature c, Player p) {
        Set<Player> pList = null;
        if (this.attacked.containsKey(c)) {
            pList = this.attacked.get(c);
        } else {
            pList = new HashSet<Player>();
            this.attacked.put(c, pList);
        }
        pList.add(p);
    }
    
    @ThreadSafe
    public void addCrowdCreature(CrowdCreature c) {
        this.crowdCreatureSet.add(c);
    }
    
    @ThreadSafe
    public void despawn(CrowdCreature c) {
        
        if (c.getEntity() instanceof Wolf) {
            Wolf w = (Wolf) c.getEntity();
            if (w.isTamed()) {
                return;
            }
        }
        
        this.crowdCreatureSet.remove(c);
        this.removeAllAttacked(c);
        c.getEntity().remove();
    }
    
    public void generateDefaults() {
        for (CreatureType t : CreatureType.values()) {
            BaseInfo info = new BaseInfo(Nature.PASSIVE, Nature.PASSIVE, 0, 10);
            
            this.setInfo(info, t);
        }
    }
    
    @ThreadSafe
    public Set<Player> getAttackingPlayers(CrowdCreature c) {
        return this.attacked.get(c);
    }
    
    @ThreadSafe
    public BaseInfo getBaseInfo(CreatureType type) {
        if (this.baseInfo.containsKey(type)) {
            return this.baseInfo.get(type);
        } else {
            BaseInfo info = new BaseInfo(Nature.PASSIVE, Nature.PASSIVE, 0, 10);
            this.setInfo(info, type);
            return info;
        }
    }
    
    @ThreadSafe
    public int getCreatureCount() {
        return this.crowdCreatureSet.size();
    }
    
    @ThreadSafe
    public int getCreatureCount(CreatureType type) {
        Iterator<CrowdCreature> i = this.crowdCreatureSet.iterator();
        
        int count = 0;
        while (i.hasNext()) {
            CrowdCreature c = i.next();
            if (c.getType() == type) {
                count++;
            }
        }
        
        return count;
    }
    
    @ThreadSafe
    public CreatureType getCreatureType(LivingEntity entity) {
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
        }
        return CreatureType.MONSTER;
    }
    
    @ThreadSafe
    public CrowdCreature getCrowdCreature(LivingEntity entity) {
        
        if (entity instanceof Player) {
            return null;
        }
        
        Iterator<CrowdCreature> i = this.crowdCreatureSet.iterator();
        
        while (i.hasNext()) {
            CrowdCreature c = i.next();
            
            if (c.getEntity() == entity) {
                return c;
            }
        }
        
        CreatureType cType = this.getCreatureType(entity);
        BaseInfo info = this.getBaseInfo(cType);
        
        if (info != null) {
            CrowdCreature c = new CrowdCreature(entity, cType, info);
            this.addCrowdCreature(c);
            return c;
        }
        
        return null;
    }
    
    @ThreadSafe
    public Set<CrowdCreature> getCrowdCreatures() {
        return Collections.unmodifiableSet(this.crowdCreatureSet);
    }
    
    @ThreadSafe
    public Set<CreatureType> getEnabledCreatureTypes() {
        return Collections.unmodifiableSet(this.enabledCreatures);
    }
    
    @ThreadSafe
    public World getWorld() {
        return this.world;
    }
    
    public boolean isDay() {
        return (this.world.getTime() < 12000) || (this.world.getTime() == 24000);
    }
    
    @ThreadSafe
    public void killAll() {
        Iterator<CrowdCreature> i = this.crowdCreatureSet.iterator();
        
        while (i.hasNext()) {
            CrowdCreature c = i.next();
            i.remove();
            this.removeAllAttacked(c);
            c.getEntity().remove();
        }
    }
    
    @ThreadSafe
    public void killAll(CreatureType type) {
        Iterator<CrowdCreature> i = this.crowdCreatureSet.iterator();
        
        while (i.hasNext()) {
            CrowdCreature c = i.next();
            if (c.getType() == type) {
                i.remove();
                this.removeAllAttacked(c);
                c.getEntity().remove();
            }
        }
    }
    
    @ThreadSafe
    public void removeAllAttacked(CrowdCreature c) {
        this.attacked.remove(c);
    }
    
    @ThreadSafe
    public void removeAttacked(CrowdCreature c, Player p) {
        if (this.attacked.containsKey(c)) {
            Set<Player> pList = this.attacked.get(c);
            pList.remove(p);
            this.attacked.put(c, pList);
        }
    }
    
    @ThreadSafe
    public void removePlayer(Player p) {
        Iterator<Entry<CrowdCreature, Set<Player>>> i = this.attacked.entrySet().iterator();
        while (i.hasNext()) {
            CrowdCreature c = i.next().getKey();
            if (this.attacked.get(c) != null) {
                this.attacked.get(c).remove(p);
            }
        }
    }
    
    public void run() {
        
        // Despawning code
        
        Iterator<CrowdCreature> i = this.crowdCreatureSet.iterator();
        
        while (i.hasNext()) {
            
            CrowdCreature c = i.next();
            LivingEntity e = c.getEntity();
            
            boolean keep = false;
            
            for (Player p : this.world.getPlayers()) {
                double deltax = Math.abs(e.getLocation().getX() - p.getLocation().getX());
                double deltay = Math.abs(e.getLocation().getY() - p.getLocation().getY());
                double deltaz = Math.abs(e.getLocation().getZ() - p.getLocation().getZ());
                double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));
                
                if (distance < this.plugin.getDespawnDistance()) {
                    if (c.getIdleTicks() < 5) { // 5 Seconds of idle time with 1% chance to despawn
                        keep = true;
                    } else {
                        if (this.rand.nextFloat() > this.plugin.getIdleDespawnChance()) { // Chance of despawning when idle
                            keep = true;
                        }
                    }
                }
            }
            
            if (!keep) {
                this.despawn(c);
                return;
            }
            
            if (e instanceof Creature) {
                
                LivingEntity target = ((Creature) e).getTarget();
                
                if (target != null) {
                    double deltax = Math.abs(e.getLocation().getX() - target.getLocation().getX());
                    double deltay = Math.abs(e.getLocation().getY() - target.getLocation().getY());
                    double deltaz = Math.abs(e.getLocation().getZ() - target.getLocation().getZ());
                    double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));
                    
                    if (distance > c.getBaseInfo().getTargetDistance()) {
                        ((Creature) e).setTarget(null);
                        
                        if (target instanceof Player) {
                            this.removeAttacked(c, (Player) target);
                        }
                    }
                    
                }
            }
            
        }
    }
    
    public void setInfo(BaseInfo info, CreatureType type) {
        Iterator<CrowdCreature> i = this.crowdCreatureSet.iterator();
        
        while (i.hasNext()) {
            CrowdCreature creature = i.next();
            
            if (creature.getType() == type) {
                creature.setBaseInfo(info);
            }
        }
        
        this.baseInfo.put(type, info);
        
        this.config.load();
        info.save(this.config, "mobs." + type.toString());
        this.config.save();
        
        if (info.isEnabled()) {
            this.enabledCreatures.add(type);
        } else {
            this.enabledCreatures.remove(type);
        }
    }
    
    public boolean shouldBurn(Location loc) {
        if (this.isDay()) {
            if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).getLightLevel() >= 15) {
                if (this.world.getHighestBlockYAt(loc) == loc.getBlockY()) {
                    return true;
                }
            }
            
        }
        return false;
    }
}
