package com.elBukkit.bukkit.plugins.crowd.creature;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
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

import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.elBukkit.bukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.bukkit.plugins.crowd.ThreadSafe;

/*
 * Handles retrieving custom creature info from a standard CreatureType
 * 
 * @author Andrew Querol(WinSock)
 */

public class CreatureHandler implements Runnable {

	private ConcurrentHashMap<CrowdCreature, Set<Player>> attacked;
	private Set<CrowdCreature> crowdCreatureSet;
	private sqlCore dbManage;
	private ConcurrentHashMap<CreatureType, BaseInfo> baseInfo;
	private Set<CreatureType> enabledCreatures;
	private MovementHandler movementHandler;
	private SpawnHandler spawnHandler;
	private World world;

	public CreatureHandler(sqlCore dbManage, World w, CrowdControlPlugin plugin) throws SQLException {
		this.dbManage = dbManage;
		this.world = w;
		baseInfo = new ConcurrentHashMap<CreatureType, BaseInfo>();
		crowdCreatureSet = Collections.newSetFromMap(new ConcurrentHashMap<CrowdCreature,Boolean>());
		enabledCreatures = Collections.newSetFromMap(new ConcurrentHashMap<CreatureType,Boolean>());
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
				
				if (enabled) {
					enabledCreatures.add(type);
				}
					
			}
		}
		dbManage.close();

		spawnHandler = new SpawnHandler(plugin, world, this);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, spawnHandler, 0, 20);

		movementHandler = new MovementHandler(plugin, this);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, movementHandler, 0, 20);
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

	public void generateDefaults() throws SQLException {
		for (CreatureType t : CreatureType.values()) {
			BaseInfo info = new BaseInfo(Nature.Passive, Nature.Passive, 0, 0, 10);

			setInfo(info, t);
		}
	}

	@ThreadSafe
	public Set<Player> getAttackingPlayers(LivingEntity entity) {
		return this.attacked.get(entity);
	}

	@ThreadSafe
	public BaseInfo getBaseInfo(CreatureType type) {
		return baseInfo.get(type);
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
		Iterator<CrowdCreature> i = crowdCreatureSet.iterator();

		while (i.hasNext()) {
			CrowdCreature c = i.next();

			if (c.getEntity() == entity) {
				return c;
			}
		}

		return null;
	}
	
	@ThreadSafe
	public void addCrowdCreature(CrowdCreature c) {
		this.crowdCreatureSet.add(c);
	}

	@ThreadSafe
	public Set<CrowdCreature> getCrowdCreatures() {
		return crowdCreatureSet;
	}

	@ThreadSafe
	public Set<CreatureType> getEnabledCreatureTypes() {
		return enabledCreatures;
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
		attacked.remove(c);
		c.getEntity().remove();
	}

	@ThreadSafe
	public void killAll() {
		Iterator<CrowdCreature> i = crowdCreatureSet.iterator();

		while (i.hasNext()) {
			CrowdCreature c = i.next();
			i.remove();
			attacked.remove(c);
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
				attacked.remove(c);
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

			if (!world.getLivingEntities().contains(e)) {
				e.remove();
				i.remove();
			} else if (e != null) {
				if (c.getHealth() <= 0) {
					e.damage(9999);
					i.remove();
				}

				if (e.isDead()) {
					e.remove();
					i.remove();
				}
			} else {
				i.remove();
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
				kill(c);
			}

			if (attacked.contains(e)) {
				Set<Player> players = attacked.get(e);
				if (players.size() > 0) {
					for (Player p : players) {
						double deltax = Math.abs(e.getLocation().getX() - p.getLocation().getX());
						double deltay = Math.abs(e.getLocation().getY() - p.getLocation().getY());
						double deltaz = Math.abs(e.getLocation().getZ() - p.getLocation().getZ());
						double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));

						if (distance > c.getBaseInfo().getTargetDistance()) {
							players.remove(p);

							if (e instanceof Creature) {
								Creature creature = (Creature) e;
								if (creature.getTarget() != null) {
									creature.setTarget(null);
								}
							}
						}
					}
				}
			}
		}

		if (world.getPlayers().size() <= 0) {
			killAll();
		}
	}
	
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
			String updateSQL = "UPDATE creatureInfo SET NatureDay = '" + info.getCreatureNatureDay().toString() + "', NatureNight = '" + info.getCreatureNatureNight().toString() + "', CollisionDmg = '" + String.valueOf(info.getCollisionDamage()) + "', MiscDmg = '" + String.valueOf(info.getMiscDamage()) + "', BurnDay = '" + String.valueOf(info.isBurnDay()) + "', Health = '" + String.valueOf(info.getHealth()) + "', TargetDistance = '" + String.valueOf(info.getTargetDistance()) + "', SpawnChance = '" + String.valueOf(info.getSpawnChance()) + "' WHERE Creature = '" + type.toString() + "';";

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
	}

	public boolean shouldBurn(Location loc) {
		if (isDay()) {
			if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).getLightLevel() > 7) {
				if (canSeeSky(loc)) {
					return true;
				}
			}

		}
		return false;
	}
}
