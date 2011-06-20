package com.elBukkit.bukkit.plugins.crowd.creature;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

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
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.elBukkit.bukkit.plugins.crowd.CrowdControlPlugin;

/*
 * Handles retrieving custom creature info from a standard CreatureType
 * 
 * @author Andrew Querol(WinSock)
 */

public class CreatureHandler implements Runnable {

	private ConcurrentHashMap<CrowdCreature, Set<Player>> attacked;
	private sqlCore dbManage;
	private ConcurrentHashMap<CreatureType, CrowdCreature> enabledCreatures;
	private ConcurrentSkipListSet<CrowdCreature> crowdCreatureSet;
	private SpawnHandler spawnHandler;
	private World world;

	public CreatureHandler(sqlCore dbManage, World w, CrowdControlPlugin plugin) throws SQLException {
		this.dbManage = dbManage;
		this.world = w;
		enabledCreatures = new ConcurrentHashMap<CreatureType, CrowdCreature>();
		crowdCreatureSet = new ConcurrentSkipListSet<CrowdCreature>();
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
				CreatureType type = CreatureType.valueOf(rs.getString(2));
				CrowdCreature info = new CrowdCreature(Nature.valueOf(rs.getString(3)), Nature.valueOf(rs.getString(4)), Integer.parseInt(rs.getString(5)), Integer.parseInt(rs.getString(6)), Integer.parseInt(rs.getString(8)), Integer.parseInt(rs.getString(9)), Boolean.parseBoolean(rs.getString(7)), Float.parseFloat(rs.getString(10)), type, Boolean.parseBoolean(rs.getString(11)));

				enabledCreatures.put(type, info);
			}
		}
		dbManage.close();

		spawnHandler = new SpawnHandler(plugin, world, this);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, spawnHandler, 0, 20);
	}

	public void addAttacked(CrowdCreature c, Player p) {
		if (this.attacked.containsKey(c)) {
			Set<Player> pList = this.attacked.get(c);
			if (pList == null) {
				pList = new HashSet<Player>();
			}
			pList.add(p);
			this.attacked.put(c, pList);
		} else {
			Set<Player> pList = new HashSet<Player>();
			pList.add(p);
			attacked.put(c, pList);
		}
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

	public void clearArrays() {
		crowdCreatureSet.clear();
		attacked.clear();
	}
	
	public CrowdCreature getBaseInfo(CreatureType type) {
		return enabledCreatures.get(type);
	}

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
			CrowdCreature info = new CrowdCreature(Nature.Passive, Nature.Passive, 0, 0, 10, t);

			setInfo(t, info);
		}
	}

	public Set<Player> getAttackingPlayers(LivingEntity entity) {
		return this.attacked.get(entity);
	}

	public int getCreatureCount() {
		return crowdCreatureSet.size();
	}

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

	public Set<CreatureType> getEnabledCreatureTypes() {
		return enabledCreatures.keySet();
	}

	public CrowdCreature getCrowdCreature(LivingEntity entity) {
		Iterator<CrowdCreature> i = crowdCreatureSet.iterator();
		
		while (i.hasNext()) {
			CrowdCreature c = i.next();
			
			if (c.getEntity() == entity) {
				return c;
			}
		}
		
		CreatureType cType = getCreatureType(entity);
		
		CrowdCreature c = enabledCreatures.get(cType);
		
		if (c != null) {
			crowdCreatureSet.add(c.create(entity));
		}
		
		return null;
	}

	public boolean isDay() {
		return world.getTime() < 12000 || world.getTime() == 24000;
	}

	public boolean isTransparentBlock(Block block) {
		if (block.getType() != Material.AIR || block.getType() != Material.LEAVES) {
			return false;
		} else {
			return true;
		}
	}

	public void kill(CrowdCreature c) {
		crowdCreatureSet.remove(c);
		attacked.remove(c);
		c.getEntity().remove();
	}

	public void killAll() {
		Iterator<CrowdCreature> i = crowdCreatureSet.iterator();
		
		while (i.hasNext()) {
			CrowdCreature c = i.next();
			i.remove();
			attacked.remove(c);
			c.getEntity().remove();
		}
	}

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

	public void removeAllAttacked(CrowdCreature c) {
		this.attacked.remove(c);
	}

	public void removeAttacked(CrowdCreature c, Player p) {
		if (this.attacked.containsKey(c)) {
			Set<Player> pList = this.attacked.get(c);
			pList.remove(p);
			this.attacked.put(c, pList);
		}
	}

	public void removePlayer(Player p) {
		Iterator<CrowdCreature> i = attacked.keySet().iterator();
		while(i.hasNext()) {
			CrowdCreature c = i.next();
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

			Set<Player> players = attacked.get(e);

			if (players != null) {
				if (players.size() > 0) {
					for (Player p : players) {
						double deltax = Math.abs(e.getLocation().getX() - p.getLocation().getX());
						double deltay = Math.abs(e.getLocation().getY() - p.getLocation().getY());
						double deltaz = Math.abs(e.getLocation().getZ() - p.getLocation().getZ());
						double distance = Math.sqrt((deltax * deltax) + (deltay * deltay) + (deltaz * deltaz));

						if (distance > c.getTargetDistance()) {
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

			attacked.put(c, players);
		}

		if (world.getPlayers().size() <= 0) {
			killAll();
		}
	}

	public void setInfo(CreatureType type, CrowdCreature c) throws SQLException {
		enabledCreatures.put(type, c);

		dbManage.initialize();
		String selectSQL = "SELECT * FROM creatureInfo WHERE Creature = '" + type.toString() + "';";
		ResultSet rs = dbManage.sqlQuery(selectSQL);

		if (rs.next()) {
			// Creature type is in db
			String updateSQL = "UPDATE creatureInfo SET NatureDay = '" + c.getCreatureNatureDay().toString() + "', NatureNight = '" + c.getCreatureNatureNight().toString() + "', CollisionDmg = '" + String.valueOf(c.getCollisionDamage()) + "', MiscDmg = '" + String.valueOf(c.getMiscDamage()) + "', BurnDay = '" + String.valueOf(c.isBurnDay()) + "', Health = '" + String.valueOf(c.getHealth()) + "', TargetDistance = '" + String.valueOf(c.getTargetDistance()) + "', SpawnChance = '" + String.valueOf(c.getSpawnChance()) + "', Enabled = '" + String.valueOf(c.isEnabled()) + "' WHERE Creature = '" + type.toString() + "';";

			dbManage.updateQuery(updateSQL);
		} else {
			String addSQL = "INSERT INTO creatureInfo (Creature, NatureDay, NatureNight, CollisionDmg, MiscDmg, BurnDay, Health, TargetDistance, SpawnChance, Enabled) VALUES ('" + type.toString() + "', '" + c.getCreatureNatureDay().toString() + "', '" + c.getCreatureNatureNight().toString() + "', '" + String.valueOf(c.getCollisionDamage()) + "', '" + String.valueOf(c.getMiscDamage()) + "', '" + String.valueOf(c.isBurnDay()) + "', '" + String.valueOf(c.getHealth()) + "', '" + String.valueOf(c.getTargetDistance()) + "', '" + String.valueOf(c.getSpawnChance()) + "', '" + String.valueOf(c.isEnabled()) + "');";

			dbManage.insertQuery(addSQL);
		}
		dbManage.close();
		
		Iterator<CrowdCreature> i = crowdCreatureSet.iterator();
		
		while(i.hasNext()) {
			CrowdCreature creature = i.next();
			
			if (creature.getType() == type) {
				creature.updateBaseInfo(c);
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
