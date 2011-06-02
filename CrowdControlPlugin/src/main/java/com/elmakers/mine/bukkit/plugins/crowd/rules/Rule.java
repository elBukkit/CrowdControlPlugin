package com.elmakers.mine.bukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elmakers.mine.bukkit.plugins.crowd.SpawnInfo;

/*
 * Rule interface, basic methods required for rule checking
 * 
 * @author Andrew Querol(WinSock)
 * 
 */

public interface Rule {
	
	public boolean spawn(SpawnInfo info); // Check if creature passes 
	
	public boolean checkWorld(World world); // Check if the world is effected by this rule
	
	public boolean checkCreatureType(CreatureType type); // Check if the creature is effected by the rule
	
}
