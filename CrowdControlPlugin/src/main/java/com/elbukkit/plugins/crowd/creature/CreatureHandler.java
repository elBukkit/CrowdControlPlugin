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

    private Map<CrowdCreature, Set<Player>> attacked;
    private Map<CreatureType, BaseInfo> baseInfo;
    private Configuration config;
    private File configFile;
    private Set<CrowdCreature> crowdCreatureSet;
    private Set<CreatureType> enabledCreatures;
    private CrowdControlPlugin plugin;
    private Random rand = new Random();
    private World world;

    public CreatureHandler(World w, CrowdControlPlugin plugin) throws IOException {
        this.world = w;
        this.plugin = plugin;
        baseInfo = new ConcurrentHashMap<CreatureType, BaseInfo>();
        crowdCreatureSet = Collections.newSetFromMap(new ConcurrentHashMap<CrowdCreature, Boolean>());
        enabledCreatures = Collections.newSetFromMap(new ConcurrentHashMap<CreatureType, Boolean>());
        attacked = new ConcurrentHashMap<CrowdCreature, Set<Player>>();

        configFile = new File(plugin.getDataFolder() + File.separator + world.getName() + ".yml");
        if (!configFile.exists()) {
            File defaults = new File(plugin.getDataFolder() + File.separator + world.getEnvironment().toString() + ".yml");
            if (defaults.exists()) {
                FileUtils.copyFile(defaults, configFile);
            } else {
                if(configFile.createNewFile()) {
                    plugin.getLog().info("[CrowdControl] Created config for " + w.getName() + "!");
                }
            }
        }
        config = new Configuration(configFile);
        config.load();

        List<String> mobs = config.getKeys("mobs");
        if (mobs != null) {
            for (String mob : mobs) {
                BaseInfo info = new BaseInfo(config, "mobs." + mob);
                baseInfo.put(CreatureType.valueOf(mob.toUpperCase()), info);

                if (info.isEnabled()) {
                    enabledCreatures.add(CreatureType.valueOf(mob.toUpperCase()));
                }
            }
        } else {
            generateDefaults();
        }

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new SpawnHandler(plugin, this), 0, 10);

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new MovementHandler(plugin, this), 0, 15);

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new DamageHandler(plugin, this), 0, 20);

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {
                Iterator<CrowdCreature> i = crowdCreatureSet.iterator();
                while (i.hasNext()) {
                    CrowdCreature creature = i.next();

                    if (creature.getHealth() <= 0) {
                        kill(creature);
                    }

                    if (creature.getEntity() != null) {
                        if (creature.getEntity().isDead() || creature.getEntity().getHealth() <= 0) {
                            removeAllAttacked(creature);
                            i.remove();
                        }
                    } else {
                        removeAllAttacked(creature);
                        i.remove();
                    }
                }

            }
        }, 0, 5);
    }

    @ThreadSafe
    public void addAttacked(CrowdCreature c, Player p) {
        Set<Player> pList = null;
        if (this.attacked.containsKey(c)) {
            pList = this.attacked.get(c);
        } else {
            pList = new HashSet<Player>();
            attacked.put(c, pList);
        }
        pList.add(p);
    }

    @ThreadSafe
    public void addCrowdCreature(CrowdCreature c) {
        this.crowdCreatureSet.add(c);
    }

    @ThreadSafe
    public void damageCreature(CrowdCreature c, int damage) {
        int health = c.getHealth();
        health -= damage;
        c.setHealth(health);

        if (health <= 0) {
            removeAllAttacked(c);
            c.getEntity().damage(9999);
            crowdCreatureSet.remove(c);
        }
    }

    @ThreadSafe
    public void despawn(CrowdCreature c) {
        crowdCreatureSet.remove(c);
        removeAllAttacked(c);
        c.getEntity().remove();
    }

    public void generateDefaults() {
        for (CreatureType t : CreatureType.values()) {
            BaseInfo info = new BaseInfo(Nature.PASSIVE, Nature.PASSIVE, 0, 0, 10);

            setInfo(info, t);
        }
    }

    @ThreadSafe
    public Set<Player> getAttackingPlayers(CrowdCreature c) {
        return this.attacked.get(c);
    }

    @ThreadSafe
    public BaseInfo getBaseInfo(CreatureType type) {
        if (baseInfo.containsKey(type)) {
            return baseInfo.get(type);
        } else {
            BaseInfo info = new BaseInfo(Nature.PASSIVE, Nature.PASSIVE, 0, 0, 10);
            setInfo(info, type);
            return info;
        }
    }

    @ThreadSafe
    public int getCreatureCount() {
        return crowdCreatureSet.size();
    }

    @ThreadSafe
    public int getCreatureCount(CreatureType type) {
        Iterator<CrowdCreature> i = crowdCreatureSet.iterator();

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

        Iterator<CrowdCreature> i = crowdCreatureSet.iterator();

        while (i.hasNext()) {
            CrowdCreature c = i.next();

            if (c.getEntity() == entity) {
                return c;
            }
        }

        CreatureType cType = getCreatureType(entity);
        BaseInfo info = getBaseInfo(cType);

        if (info != null) {
            CrowdCreature c = new CrowdCreature(entity, cType, info);
            addCrowdCreature(c);
            return c;
        }

        return null;
    }

    @ThreadSafe
    public Set<CrowdCreature> getCrowdCreatures() {
        return Collections.unmodifiableSet(crowdCreatureSet);
    }

    @ThreadSafe
    public Set<CreatureType> getEnabledCreatureTypes() {
        return Collections.unmodifiableSet(enabledCreatures);
    }

    @ThreadSafe
    public World getWorld() {
        return world;
    }

    public boolean isDay() {
        return world.getTime() < 12000 || world.getTime() == 24000;
    }

    @ThreadSafe
    public void kill(CrowdCreature c) {
        crowdCreatureSet.remove(c);
        removeAllAttacked(c);
        c.getEntity().damage(200);
    }

    @ThreadSafe
    public void killAll() {
        Iterator<CrowdCreature> i = crowdCreatureSet.iterator();

        while (i.hasNext()) {
            CrowdCreature c = i.next();
            i.remove();
            removeAllAttacked(c);
            c.getEntity().remove();
        }
    }

    @ThreadSafe
    public void killAll(CreatureType type) {
        Iterator<CrowdCreature> i = crowdCreatureSet.iterator();

        while (i.hasNext()) {
            CrowdCreature c = i.next();
            if (c.getType() == type) {
                i.remove();
                removeAllAttacked(c);
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
        Iterator<Entry<CrowdCreature, Set<Player>>> i = attacked.entrySet().iterator();
        while (i.hasNext()) {
            CrowdCreature c = i.next().getKey();
            if (this.attacked.get(c) != null) {
                this.attacked.get(c).remove(p);
            }
        }
    }

    public void run() {

        // Despawning code

        Iterator<CrowdCreature> i = crowdCreatureSet.iterator();

        while (i.hasNext()) {

            CrowdCreature c = i.next();
            LivingEntity e = c.getEntity();

            boolean keep = false;

            for (Player p : world.getPlayers()) {
                double deltax = Math.abs(e.getLocation().getX() - p.getLocation().getX());
                double deltay = Math.abs(e.getLocation().getY() - p.getLocation().getY());
                double deltaz = Math.abs(e.getLocation().getZ() - p.getLocation().getZ());
                double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));

                if (distance < plugin.getDespawnDistance()) {
                    if (c.getIdleTicks() < 5) { // 5 Seconds of idle time with
                                                // 1% chance to despawn
                        keep = true;
                    } else {
                        if (rand.nextFloat() > plugin.getIdleDespawnChance()) { // Chance
                                                                                // of
                                                                                // despawning
                                                                                // when
                                                                                // idle
                            keep = true;
                        }
                    }
                }
            }

            if (!keep) {
                despawn(c);
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
                            removeAttacked(c, (Player) target);
                        }
                    }

                }
            }

        }

        if (world.getPlayers().size() <= 0) {
            killAll();
        }
    }

    public void setInfo(BaseInfo info, CreatureType type) {
        Iterator<CrowdCreature> i = crowdCreatureSet.iterator();

        while (i.hasNext()) {
            CrowdCreature creature = i.next();

            if (creature.getType() == type) {
                creature.setBaseInfo(info);
            }
        }

        baseInfo.put(type, info);

        config.load();
        info.save(config, "mobs." + type.toString());
        config.save();

        if (info.isEnabled()) {
            enabledCreatures.add(type);
        } else {
            enabledCreatures.remove(type);
        }
    }

    public boolean shouldBurn(Location loc) {
        if (isDay()) {
            if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).getLightLevel() >= 15) {
                if (world.getHighestBlockYAt(loc) == loc.getBlockY()) {
                    return true;
                }
            }

        }
        return false;
    }
}
