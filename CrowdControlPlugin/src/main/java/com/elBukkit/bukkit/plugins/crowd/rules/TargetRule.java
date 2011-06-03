package com.elBukkit.bukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.TargetInfo;

/*
 * Target Rule interface, basic methods required for target checking
 * 
 * @author Andrew Querol(WinSock)
 * 
 */

public interface TargetRule {

	public boolean target(TargetInfo info); // Check if creature can target

	public boolean checkWorld(World world); // Check if the world is effected by
											// this rule

	public boolean checkCreatureType(CreatureType type); // Check if the
															// creature is
															// effected by the
															// rule

}
