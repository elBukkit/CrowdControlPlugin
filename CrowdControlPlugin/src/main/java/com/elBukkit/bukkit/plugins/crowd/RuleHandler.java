package com.elBukkit.bukkit.plugins.crowd;

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
import org.bukkit.entity.Entity;
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
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.WaterMob;
import org.bukkit.entity.Zombie;

import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.elBukkit.bukkit.plugins.crowd.rules.Rule;
import com.elBukkit.bukkit.plugins.crowd.rules.Type;

/*
 * Handles all of the rules and checks if the triggering creatures passes
 * 
 * @author Andrew Querol(WinSock)
 */

public class RuleHandler {

	private Map<Rule, Integer> rules;
	
	private sqlCore dbManage;

	// TODO Add when entity movement events are added, feature request #157
	// private Set<SpawnRule> movmentRules;

	public RuleHandler(sqlCore dbManage) throws SQLException, ClassNotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		rules = new HashMap<Rule, Integer>();
		
		this.dbManage = dbManage;
		
		dbManage.initialize();
		if (!dbManage.checkTable("Rules"))
		{
			String createDB = "CREATE TABLE spawnRules" +
				"(" +
				"Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"Rule VARCHAR(255), " +
				"Worlds VARCHAR(255), " +
				"Creatures VARCHAR(255), " +
				"Data VARCHAR(255)" +
				");";
			dbManage.createTable(createDB);
		}
		else
		{
			String selectSQL = "SELECT * FROM spawnRules;";
			ResultSet rs = dbManage.sqlQuery(selectSQL);
			
			while(rs.next())
			{
				String ruleClass, worlds, creatures, data;
				int id = rs.getInt(1);
				ruleClass = rs.getString(2);
				worlds = rs.getString(3);
				creatures = rs.getString(4);
				data = rs.getString(5);
				
				String[] worldArray = worlds.split(" ");
				Set<World> worldSet = new HashSet<World>();
				for (String s : worldArray) {
					worldSet.add(Bukkit.getServer().getWorld(s));
				}
				
				Class<? extends Rule> rule = Class.forName(ruleClass).asSubclass(Rule.class);
				Constructor<? extends Rule> c = rule.getDeclaredConstructor(String.class,Set.class,CreatureType.class);
				Object classObj = c.newInstance(data,worldSet,CreatureType.valueOf(creatures));
				
				if(classObj instanceof Rule){
					AddRule((Rule)classObj,id);
				} else {
					System.out.println("Invalid Class: " + rule.getSimpleName() + " in Database!");
					String removeSQL = "DELETE * FROM spawnRules WHERE " +
						"Id = '" + String.valueOf(id) +
						"';";
					dbManage.deleteQuery(removeSQL);
				}
			}
			
		}
		dbManage.close();
	}

	public void AddRule(Rule rule) throws SQLException {

		String worlds = "";
		
		for(World w : rule.getWorlds())
		{
			worlds += w.getName() + " ";
		}
		
		String addRuleSQL = "INSERT INTO spawnRules (Rule,Worlds,Creatures,Data) " +
				"VALUES(" + rule.getClass().getName() + 
				", " + worlds +
				", " + rule.getCreatureType().toString() +
				", " + rule.getData() + 
				");";
		
		dbManage.initialize();
		dbManage.insertQuery(addRuleSQL);
		rules.put(rule, dbManage.sqlQuery("SELECT last_insert_rowid();").getInt(0));
		dbManage.close();
	}
	
	public void AddRule(Rule rule, int id) {
		rules.put(rule, id);
	}

	public void RemoveRule(Rule rule) {
		String removeSQL = "DELETE * FROM spawnRules WHERE " +
			"Id = '" + String.valueOf(rules.get(rule)) +
			"';";
		dbManage.deleteQuery(removeSQL);
		dbManage.close();
		rules.remove(rule);
	}

	public boolean passesRules(Info info, Type type) {
		for (Rule r : rules.keySet()) {
			if (r.getType().equals(type)){
			if (r.checkWorld(info.getLocation().getWorld())) {
				if (r.checkCreatureType(info.getType())) {
					if (r.check(info)) {
						return true;
					}
					return false;
				}
			}
			}
		}
		return true;
	}
}
