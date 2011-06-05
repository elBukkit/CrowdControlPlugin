package com.elBukkit.bukkit.plugins.crowd;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.alta189.sqlLibrary.SQLite.sqlCore;

/*
 * CrowdControl plugin
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdControlPlugin extends JavaPlugin {

	private CrowdEntityListener entityListener = new CrowdEntityListener(this);
	private PluginDescriptionFile pdf;

	public RuleHandler ruleHandler;
	
	public sqlCore dbManage; // import SQLite lib
	public String prefix = "[CrowdControl]";
	public String dbName = "ccdb";

	public void onDisable() {
		System.out.println(pdf.getFullName() + " is disabled!");

	}

	public void onEnable() {
		pdf = this.getDescription();
		System.out.println(pdf.getFullName() + " is enabled!");
		
		if(!this.getDataFolder().exists())
			this.getDataFolder().mkdirs(); // Create dir if it doesn't exist
		
		dbManage = new sqlCore(this.getServer().getLogger(), this.prefix, this.dbName, this.getDataFolder().getAbsolutePath());
		try
		{
			ruleHandler = new RuleHandler(dbManage);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			this.setEnabled(false);
			return;
		}

		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest,
				this);
	}
}
