package com.elBukkit.mine.bukkit.plugins.crowd.creature;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.CreatureType;

/*
 * Handles retrieving custom creature info from a standard CreatureType
 * 
 * @author Andrew Querol(WinSock)
 */

public class CreatureHandler {

	private Map<CreatureType,CreatureInfo> creatureMap;
	
	public CreatureHandler()
	{
		creatureMap = new HashMap<CreatureType,CreatureInfo>();
	}
	
	public CreatureInfo getInfo(CreatureType type) throws Exception
	{
		if (creatureMap.containsKey(type))
		{
			return creatureMap.get(type);
		}
		throw new Exception("Cannot find creature"); // This should hopefully never happen
	}
	
	public void setInfo(CreatureType type, CreatureInfo info)
	{
		creatureMap.put(type, info);
	}
	
	public void generateDefaults()
	{
		// TODO add all of the default creature info here.
	}
	
}
