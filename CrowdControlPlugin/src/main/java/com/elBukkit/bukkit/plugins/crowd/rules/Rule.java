package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * Rule class, basic methods required for checking
 * 
 * @author Andrew Querol(WinSock)
 * 
 */

public class Rule {

	private Set<World> worlds;
	private CreatureType type;
	protected Type ruleType;
	
	public Rule(Set<World> worlds, CreatureType type)
	{
		this.worlds = worlds;
		this.type = type;
	}
	
	public boolean check(Info info){ return true; } // Check if creature passes

	public boolean checkWorld(World world) { // Check if the world is effected by this rule
		if (worlds.contains(world))
		{
			return true;
		}
		return false;
	} 

	public boolean checkCreatureType(CreatureType type) { // Check if the creature is effected by the rule
		if(this.type == type) {
			return true;
		}
		return false;
	} 
	
	// Classes used for saving data
	public CreatureType getCreatureType()
	{
		return type;
	}
	
	public Set<World> getWorlds()
	{
		return worlds;
	}
	
	public Type getType() { return ruleType; }
	
	public String getData() { return ""; }
	
	public void init(String data) { }

}
