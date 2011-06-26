package com.elBukkit.plugins.crowd.creature;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
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

import com.alta189.sqlLibraryV2.SQLite.sqlCore;
import com.elBukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.plugins.crowd.ThreadSafe;

/*
 * Handles retrieving custom creature info from a standard CreatureType
 * 
 * @author Andrew Querol(WinSock)
 */

public class CreatureHandler implements Runnable {

	private ConcurrentHashMap<CrowdCreature, Set<Player>> attacked;
	private ConcurrentHashMap<CreatureType, BaseInfo> baseInfo;
	private Set<CrowdCreature> crowdCreatureSet;
	private sqlCore dbManage;
	private Set<CreatureType> enabledCreatures;
	private Random rand = new Random();
	private World world;
	private CrowdControlPlugin plugin;

	public CreatureHandler(sqlCore dbManage, World w, CrowdControlPlugin plugin) throws SQLException {
		this.dbManage = dbManage;
		this.world = w;
		this.plugin = plugin;
		baseInfo = new ConcurrentHashMap<CreatureType, BaseInfo>();
		crowdCreatureSet = Collections.newSetFromMap(new ConcurrentHashMap<CrowdCreature, Boolean>());
		enabledCreatures = Collections.newSetFromMap(new ConcurrentHashMap<CreatureType, Boolean>());
		attacked = new ConcurrentHashMap<CrowdCreature, Set<Player>>();

		dbManage.initialize();
		if (!dbManage.checkTable("creatureInfo")) {
			String createDB = "CREATE TABLE creatureInfo" + "(" + "Id INTEGER PRIMARY KEY AUTOINCREMENT, " + "Creature VARCHAR(255), " + "NatureDay VARCHAR(255), " + "NatureNight VARCHAR(255), " + "CollisionDmg INT(10), " + "MiscDmg INT(10)," + "BurnDay VARCHAR(5)," + "Health INT(10), " + "TargetDistance INT(10), " + "SpawnChance FLOAT(1,2), " + "Enabled VARCHAR(5)" + ");";
			dbManage.createTable(createDB);
			generateDefaults();
		} else {
			String selectSQL = "SELECT * FROM creatureInfo";

			ResultSet rs = dbManage.sqlQuery(selectSQL);

			while (rs.next()) {
				boolean enabled = Boolean.parseBoolean(rs.getString(11));

				CreatureType type = CreatureType.valueOf(rs.getString(2));
				BaseInfo info = new BaseInfo(Nature.valueOf(rs.getString(3)), Nature.valueOf(rs.getString(4)), Integer.parseInt(rs.getString(5)), Integer.parseInt(rs.getString(6)), Integer.parseInt(rs.getString(8)), Integer.parseInt(rs.getString(9)), Boolean.parseBoolean(rs.getString(7)), Float.parseFloat(rs.getString(10)));

				baseInfo.put(type, info);

				Iterator<CrowdCreature> i = crowdCreatureSet.iterator();
				while (i.hasNext()) {
					CrowdCreature creature = i.next();

					if (creature.getType() == type) {
						creature.setBaseInfo(info);
					}
				}

				if (enabled) {
					enabledCreatures.add(type);
				}

			}
		}
		dbManage.close();

		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new SpawnHandler(plugin, this), 0, 20);

		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new MovementHandler(plugin, this), 20, 20);

		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new DamageHandler(plugin, this), 40, 20);

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
		Set<Player> pList;
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
	public boolean canSeeSky(Location locI) {
		Location loc = locI.clone();
		for (int i = 128; i >= 0; i++) {
			if (!isTransparentBlock(loc.getWorld().getBlockTypeIdAt(loc.getBlockX(), i, loc.getBlockZ()))) {
				return false;
			}
		}

		return true;
	}

	@ThreadSafe
	public void clearArrays() {
		crowdCreatureSet.clear();
		attacked.clear();
	}

	@ThreadSafe
	public void clearArrays(CreatureType type) {
		Iterator<CrowdCreature> i = crowdCreatureSet.iterator();

		while (i.hasNext()) {
			CrowdCreature c = i.next();
			if (c.getType() == type) {
				i.remove();
				attacked.remove(c);
			}
		}
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

	public void generateDefaults() throws SQLException {
		for (CreatureType t : CreatureType.values()) {
			BaseInfo info = new BaseInfo(Nature.Passive, Nature.Passive, 0, 0, 10);

			setInfo(info, t);
		}
	}

	@ThreadSafe
	public Set<Player> getAttackingPlayers(CrowdCreature c) {
		return this.attacked.get(c);
	}

	@ThreadSafe
	public BaseInfo getBaseInfo(CreatureType type) {
		if (baseInfo.contains(type)) {
			return baseInfo.get(type);
		} else {
			BaseInfo info = new BaseInfo(Nature.Passive, Nature.Passive, 0, 0, 10);
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
		return crowdCreatureSet;
	}

	@ThreadSafe
	public Set<CreatureType> getEnabledCreatureTypes() {
		return enabledCreatures;
	}

	@ThreadSafe
	public World getWorld() {
		return world;
	}

	public boolean isDay() {
		return world.getTime() < 12000 || world.getTime() == 24000;
	}

	@ThreadSafe
	public boolean isTransparentBlock(int i) {
		if (i != Material.AIR.getId() || i != Material.LEAVES.getId()) {
			return false;
		} else {
			return true;
		}
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

		if (crowdCreatureSet.size() > 1000 || attacked.size() > 1000 || baseInfo.size() > 1000) {
			System.out.println(crowdCreatureSet.size());
			System.out.println(attacked.size());
			System.out.println(baseInfo.size());
		}

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
					if (c.getIdleTicks() < 5) { // 5 Seconds of idle time with 1% chance to despawn
						keep = true;
					} else {
						if (rand.nextFloat() > plugin.getIdleDespawnChance()) { // 5% Chance of despawning when idle 
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

	@ThreadSafe
	public void setEnabled(CreatureType type, boolean enabled) {
		if (enabled) {
			this.enabledCreatures.add(type);
		} else {
			this.enabledCreatures.remove(type);
		}
	}

	public void setInfo(BaseInfo info, CreatureType type) throws SQLException {
		dbManage.initialize();
		String selectSQL = "SELECT * FROM creatureInfo WHERE Creature = '" + type.toString() + "';";
		ResultSet rs = dbManage.sqlQuery(selectSQL);

		if (rs.next()) {
			// Creature type is in db
			String updateSQL = "UPDATE creatureInfo SET NatureDay = '" + info.getCreatureNatureDay().toString() + "', NatureNight = '" + info.getCreatureNatureNight().toString() + "', CollisionDmg = '" + String.valueOf(info.getCollisionDamage()) + "', MiscDmg = '" + String.valueOf(info.getMiscDamage()) + "', BurnDay = '" + String.valueOf(info.isBurnDay()) + "', Health = '" + String.valueOf(info.getHealth()) + "', TargetDistance = '" + String.valueOf(info.getTargetDistance()) + "', SpawnChance = '" + String.valueOf(info.getSpawnChance()) + "', Enabled = '" + String.valueOf(enabledCreatures.contains(type)) + "' WHERE Creature = '" + type.toString() + "';";

			dbManage.updateQuery(updateSQL);
		} else {
			String addSQL = "INSERT INTO creatureInfo (Creature, NatureDay, NatureNight, CollisionDmg, MiscDmg, BurnDay, Health, TargetDistance, SpawnChance, Enabled) VALUES ('" + type.toString() + "', '" + info.getCreatureNatureDay().toString() + "', '" + info.getCreatureNatureNight().toString() + "', '" + String.valueOf(info.getCollisionDamage()) + "', '" + String.valueOf(info.getMiscDamage()) + "', '" + String.valueOf(info.isBurnDay()) + "', '" + String.valueOf(info.getHealth()) + "', '" + String.valueOf(info.getTargetDistance()) + "', '" + String.valueOf(info.getSpawnChance()) + "', '" + String.valueOf(enabledCreatures.contains(type)) + "');";

			dbManage.insertQuery(addSQL);
		}
		dbManage.close();

		Iterator<CrowdCreature> i = crowdCreatureSet.iterator();

		while (i.hasNext()) {
			CrowdCreature creature = i.next();

			if (creature.getType() == type) {
				creature.setBaseInfo(info);
			}
		}
		
		baseInfo.put(type, info);
	}

	public boolean shouldBurn(Location loc) {
		if (isDay()) {
			if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).getLightLevel() == 12) {
				if (canSeeSky(loc)) {
					return true;
				}
			}

		}
		return false;
	}
}
