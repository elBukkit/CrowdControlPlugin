package com.elmakers.mine.bukkit.plugins.crowd;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * CrowdControl plugin
 * 
 * @author Andrew Querol(WinSock)
 * 
 */

public class CrowdControlPlugin extends JavaPlugin
{
	
	private CrowdEntityListener entityListener = new CrowdEntityListener(this);
	public RuleHandler ruleHandler = new RuleHandler();
	
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	public void onEnable() {
		// TODO Auto-generated method stub
		
		// Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest, this);
	}
}
