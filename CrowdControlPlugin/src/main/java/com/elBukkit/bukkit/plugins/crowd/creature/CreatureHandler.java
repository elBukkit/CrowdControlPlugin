package com.elBukkit.bukkit.plugins.crowd.creature;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.CreatureType;

import com.alta189.sqlLibrary.SQLite.sqlCore;

/*
 * Handles retrieving custom creature info from a standard CreatureType
 * 
 * @author Andrew Querol(WinSock)
 */

public class CreatureHandler {

	private Map<CreatureType, CreatureInfo> creatureMap;
	private sqlCore dbManage;

	public CreatureHandler(sqlCore dbManage) throws SQLException {
		this.dbManage = dbManage;
		creatureMap = new HashMap<CreatureType, CreatureInfo>();

		dbManage.initialize();
		if (!dbManage.checkTable("creatureInfo")) {
			String createDB = "CREATE TABLE creatureInfo" + "("
					+ "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "Creature VARCHAR(255), " + "NatureDay VARCHAR(255), "
					+ "NatureNight VARCHAR(255), " + "CollisionDmg INT(10), "
					+ "MiscDmg INT(10)," + "BurnDay TINYINT(1),"
					+ "Health INT(10), " + "SpawnChance FLOAT(1,2)" + ");";
			dbManage.createTable(createDB);
			generateDefaults();
		} else {
			String selectSQL = "SELECT * FROM creatureInfo";

			ResultSet rs = dbManage.sqlQuery(selectSQL);

			while (rs.next()) {
				CreatureType type = CreatureType.valueOf(rs.getString(2));
				CreatureInfo info = new CreatureInfo();

				info.setCreatureNatureDay(Nature.valueOf(rs.getString(3)));
				info.setCreatureNatureNight(Nature.valueOf(rs.getString(4)));
				info.setCollisionDamage(rs.getInt(5));
				info.setMiscDamage(rs.getInt(6));
				info.setBurnDay(rs.getBoolean(7));
				info.setHealth(rs.getInt(8));
				info.setSpawnChance(rs.getFloat(9));

				creatureMap.put(type, info);
			}
		}
		dbManage.close();
	}

	public CreatureInfo getInfo(CreatureType type) throws Exception {
		if (creatureMap.containsKey(type)) {
			return creatureMap.get(type);
		}
		throw new Exception("Cannot find creature"); // This should hopefully
														// never happen
	}

	public void setInfo(CreatureType type, CreatureInfo info)
			throws SQLException {
		creatureMap.put(type, info);

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
					+ "', SpawnChance = '"
					+ String.valueOf(info.getSpawnChance())
					+ "' WHERE Creature = '" + type.toString() + "';";

			dbManage.updateQuery(updateSQL);
		} else {
			String addSQL = "INSERT INTO creatureInfo (Creature, NatureDay, NatureNight, CollisionDmg, MiscDmg, BurnDay, Health, SpawnChance) VALUES ('"
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
					+ String.valueOf(info.getSpawnChance()) + "');";

			dbManage.insertQuery(addSQL);
		}
		dbManage.close();
	}

	public void generateDefaults() throws SQLException {
		for (CreatureType t : CreatureType.values()) {
			CreatureInfo info = new CreatureInfo();

			info.setCollisionDamage(0);
			info.setCreatureNatureDay(Nature.Passive);
			info.setCreatureNatureNight(Nature.Neutral);
			info.setHealth(10);
			info.setMiscDamage(0);

			setInfo(t, info);
		}
	}

}
