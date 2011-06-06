package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * TODO Finish the rule
 * 
 * Set maximum number of a creature type
 * 
 *  @author Andrew Querol(WinSock)
 */

public class MaxRule extends Rule {
	
	private int maxMobs;
	
	public MaxRule(Set<World> worlds, CreatureType type) {
		super(worlds, type);
		this.ruleType = Type.Spawn;
	}
	
	@Override
	public void init(String data)
	{
		// TODO Finish init()
	}
	
	@Override
	public boolean check(Info info)
	{
		return true; // TODO Finish rule :)
	}

	@Override
	public String getData() {
		return String.valueOf(maxMobs);
	}

}
