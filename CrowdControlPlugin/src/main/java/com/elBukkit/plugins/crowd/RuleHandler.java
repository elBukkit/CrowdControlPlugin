package com.elBukkit.plugins.crowd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.plugins.crowd.rules.Rule;
import com.elBukkit.plugins.crowd.rules.Type;
import com.elBukkit.plugins.crowd.sqlLibrary.SQLite.sqlCore;

/*
 * Handles all of the rules and checks if the triggering creatures passes
 * 
 * @author Andrew Querol(WinSock)
 */

public class RuleHandler {

	private sqlCore dbManage;

	private Map<Integer, Rule> rules;

	public RuleHandler(sqlCore dbManage, CrowdControlPlugin plugin) throws SQLException, ClassNotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		rules = new HashMap<Integer, Rule>();

		this.dbManage = dbManage;

		dbManage.initialize();
		if (!dbManage.checkTable("spawnRules")) {
			String createDB = "CREATE TABLE spawnRules" + "(" + "Id INTEGER PRIMARY KEY AUTOINCREMENT, " + "Rule VARCHAR(255), " + "Worlds VARCHAR(255), " + "Creatures VARCHAR(255), " + "Data VARCHAR(255)" + ");";
			dbManage.createTable(createDB);
		} else {
			String selectSQL = "SELECT * FROM spawnRules;";
			ResultSet rs = dbManage.sqlQuery(selectSQL);

			while (rs.next()) {
				String ruleClass, world, creatures, data;
				int id = rs.getInt(1);
				ruleClass = rs.getString(2);
				world = rs.getString(3);
				creatures = rs.getString(4);
				data = rs.getString(5);

				Class<? extends Rule> rule = Class.forName(ruleClass).asSubclass(Rule.class);
				Constructor<? extends Rule> c = rule.getDeclaredConstructor(World.class, CreatureType.class, CrowdControlPlugin.class);
				Object classObj = c.newInstance(Bukkit.getServer().getWorld(world), CreatureType.valueOf(creatures), plugin);

				if (classObj instanceof Rule) {
					((Rule) classObj).init(data);
					AddRule((Rule) classObj, id);
				} else {
					System.out.println("Invalid Class: " + rule.getSimpleName() + " in Database!");
					String removeSQL = "DELETE * FROM spawnRules WHERE " + "Id = '" + String.valueOf(id) + "';";
					dbManage.deleteQuery(removeSQL);
				}
			}

		}
		dbManage.close();
	}

	public void AddRule(Rule rule) throws SQLException {

		String addRuleSQL = "INSERT INTO spawnRules (Rule,Worlds,Creatures,Data) " + "VALUES('" + rule.getClass().getName() + "', '" + rule.getWorld().getName() + "', '" + rule.getCreatureType().toString() + "', '" + rule.getData() + "');";

		dbManage.initialize();
		dbManage.insertQuery(addRuleSQL);
		ResultSet rs = dbManage.sqlQuery("SELECT last_insert_rowid() FROM spawnRules;");
		if (rs.next()) {
			rules.put(rs.getInt(1), rule);
		} else {
			System.out.println("Error adding rule!");
		}
		dbManage.close();
	}

	public void AddRule(Rule rule, int id) {
		rules.put(id, rule);
	}

	public Map<Integer, Rule> getRules() {
		return rules;
	}

	public boolean passesRules(Info info, Type type) {
		for (Rule r : rules.values()) {
			if (r.getType().equals(type)) {
				if (r.checkWorld(info.getLocation().getWorld())) {
					if (r.checkCreatureType(info.getType())) {
						if (!r.check(info)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public void rebuildDB() throws SQLException {
		String dropSQL = "DROP TABLE IF EXISTS spawnRules";
		String createDB = "CREATE TABLE spawnRules" + "(" + "Id INTEGER PRIMARY KEY AUTOINCREMENT, " + "Rule VARCHAR(255), " + "Worlds VARCHAR(255), " + "Creatures VARCHAR(255), " + "Data VARCHAR(255)" + ");";
		Set<Rule> tempRules = new HashSet<Rule>(rules.values());
		rules.clear();

		dbManage.initialize();
		dbManage.deleteQuery(dropSQL);
		dbManage.createTable(createDB);
		dbManage.close();

		for (Rule r : tempRules) {
			this.AddRule(r);
		}
	}

	public void RemoveRule(int id) {
		rules.remove(id);
	}
}
