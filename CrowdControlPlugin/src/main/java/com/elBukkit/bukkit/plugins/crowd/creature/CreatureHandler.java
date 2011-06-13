package com.elBukkit.bukkit.plugins.crowd.creature;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.bukkit.entity.Entity;
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

/*
 * Handles retrieving custom creature info from a standard CreatureType
 * 
 * @author Andrew Querol(WinSock)
 */

public class CreatureHandler implements Runnable {

	World world;
	private Map<CreatureType, CreatureInfo> creatureTypeMap;
	private Map<Creature, CreatureInfo> creatureInfoMap;
	private Map<Creature, Set<Player>> attacked;
	private sqlCore dbManage;

	public CreatureHandler(sqlCore dbManage, World w) throws SQLException {
		this.dbManage = dbManage;
		this.world = w;
		creatureTypeMap = new HashMap<CreatureType, CreatureInfo>();
		creatureInfoMap = new HashMap<Creature, CreatureInfo>();
		attacked = new HashMap<Creature, Set<Player>>();

		dbManage.initialize();
		if (!dbManage.checkTable("creatureInfo")) {
			String createDB = "CREATE TABLE creatureInfo" + "("
					+ "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "Creature VARCHAR(255), " + "NatureDay VARCHAR(255), "
					+ "NatureNight VARCHAR(255), " + "CollisionDmg INT(10), "
					+ "MiscDmg INT(10)," + "BurnDay TINYINT(1),"
					+ "Health INT(10), " + "TargetDistance INT(10), "
					+ "SpawnChance FLOAT(1,2)" + ");";
			dbManage.createTable(createDB);
			generateDefaults();
		} else {
			String selectSQL = "SELECT * FROM creatureInfo";

			ResultSet rs = dbManage.sqlQuery(selectSQL);

			while (rs.next()) {
				CreatureType type = CreatureType.valueOf(rs.getString(2));
				CreatureInfo info = new CreatureInfo(Nature.valueOf(rs
						.getString(3)), Nature.valueOf(rs.getString(4)),
						Integer.parseInt(rs.getString(5)), Integer.parseInt(rs
								.getString(6)), Integer.parseInt(rs
								.getString(8)), Integer.parseInt(rs
								.getString(9)), Boolean.parseBoolean(rs
								.getString(7)), Float.parseFloat(rs
								.getString(10)), type);

				creatureTypeMap.put(type, info);
			}
		}
		dbManage.close();
	}

	public CreatureInfo getInfo(CreatureType type) {
		return creatureTypeMap.get(type);
	}

	public Set<Player> getAttackingPlayers(Creature c) {
		return this.attacked.get(c);
	}

	public void addAttacked(Creature c, Player p) {
		if (this.attacked.containsKey(c)) {
			Set<Player> pList = this.attacked.get(c);
			pList.add(p);
			this.attacked.put(c, pList);
		} else {
			Set<Player> pList = new HashSet<Player>();
			pList.add(p);
			attacked.put(c, pList);
		}
	}

	public void killAll() {
		for (Creature c : creatureInfoMap.keySet()) {
			this.damageCreature(c, creatureInfoMap.get(c).getHealth());
		}
	}

	public void killAll(CreatureType type) {
		for (Creature c : creatureInfoMap.keySet()) {
			if (creatureInfoMap.get(c).getType() == type) {
				this.damageCreature(c, creatureInfoMap.get(c).getHealth());
			}
		}
	}

	public void kill(Creature c) {
		this.damageCreature(c, creatureInfoMap.get(c).getHealth());
	}

	public void removeAttacked(Creature c, Player p) {
		if (this.attacked.containsKey(c)) {
			Set<Player> pList = this.attacked.get(c);
			pList.remove(p);
			this.attacked.put(c, pList);
		}
	}

	public void removePlayer(Player p) {
		for (Creature c : this.attacked.keySet()) {
			this.attacked.get(c).remove(p);
		}
	}

	public void addCreature(Creature c) {
		CreatureInfo cInfo = getInfo(getCreatureType((Entity) c));

		if (cInfo != null) {
			creatureInfoMap.put(c, cInfo.copy());
		}
	}

	public Integer getHealth(Creature c) {

		if (!creatureInfoMap.containsKey(c)) {
			addCreature(c);
		}

		return creatureInfoMap.get(c).getHealth();
	}

	public void damageCreature(Creature c, int damage) {

		if (!creatureInfoMap.containsKey(c)) {
			addCreature(c);
		}

		CreatureInfo cInfo = creatureInfoMap.get(c);
		int health = cInfo.getHealth();
		health -= damage;
		cInfo.setHealth(health);

		if (health <= 0) {
			removeAllAttacked(c);
			c.setHealth(0);
			c.remove();
			creatureInfoMap.remove(c);
		}
		creatureInfoMap.put(c, cInfo);
	}

	public void removeAllAttacked(Creature c) {
		this.attacked.remove(c);
	}

	public void setInfo(CreatureType type, CreatureInfo info)
			throws SQLException {
		creatureTypeMap.put(type, info);

		dbManage.initialize();
		String selectSQL = "SELECT * FROM creatureInfo WHERE Creature = '"
				+ type.toString() + "';";
		ResultSet rs = dbManage.sqlQuery(selectSQL);

		if (rs.next()) {
			// Creature type is in db
			String updateSQL = "UPDATE creatureInfo SET NatureDay = '"
					+ info.getCreatureNatureDay().toString()
					+ "', NatureNight = '"
					+ info.getCreatureNatureNight().toString()
					+ "', CollisionDmg = '"
					+ String.valueOf(info.getCollisionDamage())
					+ "', MiscDmg = '" + String.valueOf(info.getMiscDamage())
					+ "', BurnDay = '" + (info.isBurnDay() ? 1 : 0)
					+ "', Health = '" + String.valueOf(info.getHealth())
					+ "', TargetDistance = '"
					+ String.valueOf(info.getTargetDistance())
					+ "', SpawnChance = '"
					+ String.valueOf(info.getSpawnChance())
					+ "' WHERE Creature = '" + type.toString() + "';";

			dbManage.updateQuery(updateSQL);
		} else {
			String addSQL = "INSERT INTO creatureInfo (Creature, NatureDay, NatureNight, CollisionDmg, MiscDmg, BurnDay, Health, TargetDistance, SpawnChance) VALUES ('"
					+ type.toString()
					+ "', '"
					+ info.getCreatureNatureDay().toString()
					+ "', '"
					+ info.getCreatureNatureNight().toString()
					+ "', '"
					+ String.valueOf(info.getCollisionDamage())
					+ "', '"
					+ String.valueOf(info.getMiscDamage())
					+ "', '"
					+ (info.isBurnDay() ? 1 : 0)
					+ "', '"
					+ String.valueOf(info.getHealth())
					+ "', '"
					+ String.valueOf(info.getTargetDistance())
					+ "', '"
					+ String.valueOf(info.getSpawnChance()) + "');";

			dbManage.insertQuery(addSQL);
		}
		dbManage.close();
	}

	public void generateDefaults() throws SQLException {
		for (CreatureType t : CreatureType.values()) {
			CreatureInfo info = new CreatureInfo(Nature.Passive,
					Nature.Passive, 0, 0, 10, t);

			setInfo(t, info);
		}
	}

	public void clearArrays() {
		creatureInfoMap.clear();
		attacked.clear();
	}

	public int getCreatureCount(CreatureType type) {
		int count = 0;
		for (Creature c : creatureInfoMap.keySet()) {
			if (creatureInfoMap.get(c).getType() == type) {
				count++;
			}
		}

		return count;
	}

	public int getCreatureCount() {
		return creatureInfoMap.size();
	}

	public void clearArrays(CreatureType type) {
		for (Creature c : creatureInfoMap.keySet()) {
			if (getCreatureType((Entity) c) == type) {
				creatureInfoMap.remove(c);
			}
		}
		for (Creature c : attacked.keySet()) {
			if (getCreatureType((Entity) c) == type) {
				attacked.remove(c);
			}
		}
	}

	public void run() {
		// Check for dead/ removed creatures
		for (Creature c : creatureInfoMap.keySet()) {
			if (c != null) {
				if (creatureInfoMap.get(c).getHealth() <= 0) {
					c.remove();
					c.setHealth(0);
					creatureInfoMap.remove(c);
				}
			} else {
				creatureInfoMap.remove(c);
			}
		}

		// Check for dead/ removed creatures
		for (Creature c : attacked.keySet()) {
			if (c != null) {
				if (attacked.get(c).size() <= 0) {
					c.remove();
					c.setHealth(0);
					attacked.remove(c);
				}
			} else {
				attacked.remove(c);
			}
		}
		
		// Remove non-tracked entities
		for (Entity e : world.getEntities()) {
			if (e instanceof Creature) {
				if (!creatureInfoMap.containsKey((Creature)e)){
					((Creature)e).damage(99999);
					((Creature)e).remove();
				}
			}
		}
	}

	public boolean shouldBurn(Location loc) {
		if (isDay(loc.getWorld())) {
			if (loc.getWorld()
					.getBlockAt(loc.getBlockX(), loc.getBlockY() + 1,
							loc.getBlockZ()).getLightLevel() > 7) {
				if (canSeeSky(loc)) {
					return true;
				}
			}

		}
		return false;
	}

	public boolean canSeeSky(Location loc) {
		for (int i = 128; i >= 0; i++) {
			if (isTransparentBlock(loc.getWorld().getBlockAt(loc.getBlockX(),
					i, loc.getBlockZ()))) {
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
		if (block.getType() != Material.AIR
				|| block.getType() != Material.LEAVES) {
			return false;
		} else {
			return true;
		}
	}

	public CreatureType getCreatureType(Entity entity) {
		if (entity instanceof LivingEntity) {
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
		}
		return null;
	}

}
