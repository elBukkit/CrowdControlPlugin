package com.elBukkit.bukkit.plugins.crowd.creature;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import org.bukkit.entity.Zombie;

import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.elBukkit.bukkit.plugins.crowd.CrowdControlPlugin;

/*
 * Handles retrieving custom creature info from a standard CreatureType
 * 
 * @author Andrew Querol(WinSock)
 */

public class CreatureHandler implements Runnable {

    World world;
    private Map<CreatureType, CreatureInfo> enabledCreatures;
    private Map<LivingEntity, CreatureInfo> livingEntityInfoMap;
    private Map<LivingEntity, Set<Player>> attacked;
    private sqlCore dbManage;
    Random random = new Random();
    SpawnHandler spawnHandler;

    public CreatureHandler(sqlCore dbManage, World w, CrowdControlPlugin plugin) throws SQLException {
        this.dbManage = dbManage;
        this.world = w;
        enabledCreatures = new HashMap<CreatureType, CreatureInfo>();
        livingEntityInfoMap = new HashMap<LivingEntity, CreatureInfo>();
        attacked = new HashMap<LivingEntity, Set<Player>>();

        dbManage.initialize();
        if (!dbManage.checkTable("creatureInfo")) {
            String createDB = "CREATE TABLE creatureInfo" + "(" + "Id INTEGER PRIMARY KEY AUTOINCREMENT, " + "Creature VARCHAR(255), " + "NatureDay VARCHAR(255), " + "NatureNight VARCHAR(255), " + "CollisionDmg INT(10), " + "MiscDmg INT(10)," + "BurnDay VARCHAR(5)," + "Health INT(10), " + "TargetDistance INT(10), " + "SpawnChance FLOAT(1,2), " + "Enabled VARCHAR(5)" + ");";
            dbManage.createTable(createDB);
            generateDefaults();
        } else {
            String selectSQL = "SELECT * FROM creatureInfo";

            ResultSet rs = dbManage.sqlQuery(selectSQL);

            while (rs.next()) {
                CreatureType type = CreatureType.valueOf(rs.getString(2));
                CreatureInfo info = new CreatureInfo(Nature.valueOf(rs.getString(3)), Nature.valueOf(rs.getString(4)), Integer.parseInt(rs.getString(5)), Integer.parseInt(rs.getString(6)), Integer.parseInt(rs.getString(8)), Integer.parseInt(rs.getString(9)), Boolean.parseBoolean(rs.getString(7)), Float.parseFloat(rs.getString(10)), type, Boolean.parseBoolean(rs.getString(11)));

                enabledCreatures.put(type, info);
            }
        }
        dbManage.close();

        spawnHandler = new SpawnHandler(plugin, world, this);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, spawnHandler, 0, 20);
    }

    public CreatureInfo getInfo(CreatureType type) {
        return enabledCreatures.get(type);
    }

    public Set<Player> getAttackingPlayers(LivingEntity entity) {
        return this.attacked.get(entity);
    }

    public void addAttacked(LivingEntity livingEntity, Player p) {
        if (this.attacked.containsKey(livingEntity)) {
            Set<Player> pList = this.attacked.get(livingEntity);
            if (pList == null) {
                pList = new HashSet<Player>();
            }
            pList.add(p);
            this.attacked.put(livingEntity, pList);
        } else {
            Set<Player> pList = new HashSet<Player>();
            pList.add(p);
            attacked.put(livingEntity, pList);
        }
    }

    public void killAll() {
        Set<LivingEntity> copy = new HashSet<LivingEntity>(livingEntityInfoMap.keySet());
        for (LivingEntity entity : copy) {
            livingEntityInfoMap.remove(entity);
            attacked.remove(entity);
            entity.remove();
        }
    }

    public void killAll(CreatureType type) {
        Set<LivingEntity> copy = new HashSet<LivingEntity>(livingEntityInfoMap.keySet());
        for (LivingEntity entity : copy) {
            if (livingEntityInfoMap.get(entity).getType() == type) {
                livingEntityInfoMap.remove(entity);
                attacked.remove(entity);
                entity.remove();
            }
        }
    }

    public void kill(LivingEntity entity) {
        livingEntityInfoMap.remove(entity);
        attacked.remove(entity);
        entity.remove();
    }

    public void removeAttacked(LivingEntity livingEntity, Player p) {
        if (this.attacked.containsKey(livingEntity)) {
            Set<Player> pList = this.attacked.get(livingEntity);
            pList.remove(p);
            this.attacked.put(livingEntity, pList);
        }
    }

    public void removePlayer(Player p) {
        for (LivingEntity entity : this.attacked.keySet()) {
            if (this.attacked.get(entity) != null) {
                this.attacked.get(entity).remove(p);
            }
        }
    }

    public void addLivingEntity(LivingEntity entity) {
        CreatureInfo cInfo = getInfo(getCreatureType(entity));

        if (cInfo != null) {
            livingEntityInfoMap.put(entity, cInfo.copy());
        }
    }

    public Integer getHealth(Creature c) {

        if (!livingEntityInfoMap.containsKey(c)) {
            addLivingEntity(c);
        }

        return livingEntityInfoMap.get(c).getHealth();
    }

    public void damageLivingEntity(LivingEntity entity, int damage) {

        if (!livingEntityInfoMap.containsKey(entity)) {
            addLivingEntity(entity);
        }

        CreatureInfo cInfo = livingEntityInfoMap.get(entity);
        int health = cInfo.getHealth();
        health -= damage;
        cInfo.setHealth(health);

        if (health <= 0) {
            removeAllAttacked(entity);
            entity.damage(9999);
            livingEntityInfoMap.remove(entity);
        }
        livingEntityInfoMap.put(entity, cInfo);
    }

    public void removeAllAttacked(LivingEntity entity) {
        this.attacked.remove(entity);
    }

    public void setInfo(CreatureType type, CreatureInfo info) throws SQLException {
        enabledCreatures.put(type, info);

        dbManage.initialize();
        String selectSQL = "SELECT * FROM creatureInfo WHERE Creature = '" + type.toString() + "';";
        ResultSet rs = dbManage.sqlQuery(selectSQL);

        if (rs.next()) {
            // Creature type is in db
            String updateSQL = "UPDATE creatureInfo SET NatureDay = '" + info.getCreatureNatureDay().toString() + "', NatureNight = '" + info.getCreatureNatureNight().toString() + "', CollisionDmg = '" + String.valueOf(info.getCollisionDamage()) + "', MiscDmg = '" + String.valueOf(info.getMiscDamage()) + "', BurnDay = '" + String.valueOf(info.isBurnDay()) + "', Health = '" + String.valueOf(info.getHealth()) + "', TargetDistance = '" + String.valueOf(info.getTargetDistance()) + "', SpawnChance = '" + String.valueOf(info.getSpawnChance()) + "', Enabled = '" + String.valueOf(info.isEnabled()) + "' WHERE Creature = '" + type.toString() + "';";

            dbManage.updateQuery(updateSQL);
        } else {
            String addSQL = "INSERT INTO creatureInfo (Creature, NatureDay, NatureNight, CollisionDmg, MiscDmg, BurnDay, Health, TargetDistance, SpawnChance, Enabled) VALUES ('" + type.toString() + "', '" + info.getCreatureNatureDay().toString() + "', '" + info.getCreatureNatureNight().toString() + "', '" + String.valueOf(info.getCollisionDamage()) + "', '" + String.valueOf(info.getMiscDamage()) + "', '" + String.valueOf(info.isBurnDay()) + "', '" + String.valueOf(info.getHealth()) + "', '" + String.valueOf(info.getTargetDistance()) + "', '" + String.valueOf(info.getSpawnChance()) + "', '" + String.valueOf(info.isEnabled()) + "');";

            dbManage.insertQuery(addSQL);
        }
        dbManage.close();
    }

    public void generateDefaults() throws SQLException {
        for (CreatureType t : CreatureType.values()) {
            CreatureInfo info = new CreatureInfo(Nature.Passive, Nature.Passive, 0, 0, 10, t);

            setInfo(t, info);
        }
    }

    public void clearArrays() {
        livingEntityInfoMap.clear();
        attacked.clear();
    }

    public int getCreatureCount(CreatureType type) {
        int count = 0;
        for (LivingEntity entity : livingEntityInfoMap.keySet()) {
            if (livingEntityInfoMap.get(entity).getType() == type) {
                count++;
            }
        }

        return count;
    }

    public int getCreatureCount() {
        return livingEntityInfoMap.size();
    }

    public void clearArrays(CreatureType type) {
        for (LivingEntity entity : livingEntityInfoMap.keySet()) {
            if (getCreatureType(entity) == type) {
                livingEntityInfoMap.remove(entity);
            }
        }
        for (LivingEntity entity : attacked.keySet()) {
            if (getCreatureType(entity) == type) {
                attacked.remove(entity);
            }
        }
    }

    public boolean shouldBurn(Location loc) {
        if (isDay(loc.getWorld())) {
            if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).getLightLevel() > 7) {
                if (canSeeSky(loc)) {
                    return true;
                }
            }

        }
        return false;
    }

    public boolean canSeeSky(Location loc) {
        for (int i = 128; i >= 0; i++) {
            if (isTransparentBlock(loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ()))) {
                if (loc.getBlockY() == i) {
                    return true;
                }
            } else {
                break;
            }
        }
        return false;
    }

    public boolean isDay(World world) {
        return world.getTime() < 12000 || world.getTime() == 24000;
    }

    public boolean isTransparentBlock(Block block) {
        if (block.getType() != Material.AIR || block.getType() != Material.LEAVES) {
            return false;
        } else {
            return true;
        }
    }

    public List<CreatureType> getEnabledCreatureTypes() {
        List<CreatureType> enabled = new ArrayList<CreatureType>();
        for (CreatureType cType : enabledCreatures.keySet()) {
            if (enabledCreatures.get(cType).isEnabled()) {
                enabled.add(cType);
            }
        }

        return enabled;
    }

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

    public void run() {

        // Despawning code

        Set<LivingEntity> copy = new HashSet<LivingEntity>(livingEntityInfoMap.keySet());
        for (LivingEntity e : copy) {

            if (!world.getLivingEntities().contains(e)) {
                e.remove();
                livingEntityInfoMap.remove(e);
            } else if (e != null) {
                if (livingEntityInfoMap.get(e).getHealth() <= 0) {
                    e.damage(9999);
                    livingEntityInfoMap.remove(e);
                }

                if (e.isDead()) {
                    e.remove();
                    livingEntityInfoMap.remove(e);
                }
            } else {
                livingEntityInfoMap.remove(e);
            }

            boolean keep = false;

            for (Player p : world.getPlayers()) {
                double deltax = Math.abs(e.getLocation().getX() - p.getLocation().getX());
                double deltay = Math.abs(e.getLocation().getY() - p.getLocation().getY());
                double deltaz = Math.abs(e.getLocation().getZ() - p.getLocation().getZ());
                double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));

                if (distance < 128) {
                    keep = true;
                }
            }

            if (!keep) {
                kill(e);
            }

            Set<Player> players = attacked.get(e);

            if (players != null)
                if (players.size() > 0)
                    for (Player p : players) {
                        double deltax = Math.abs(e.getLocation().getX() - p.getLocation().getX());
                        double deltay = Math.abs(e.getLocation().getY() - p.getLocation().getY());
                        double deltaz = Math.abs(e.getLocation().getZ() - p.getLocation().getZ());
                        double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));

                        if (distance > livingEntityInfoMap.get(e).getTargetDistance()) {
                            players.remove(p);

                            if (e instanceof Creature) {
                                Creature c = (Creature) e;
                                if (c.getTarget() != null) {
                                    c.setTarget(null);
                                }
                            }
                        }
                    }

            attacked.put(e, players);
        }

        if (world.getPlayers().size() <= 0) {
            killAll();
        }
    }
}
