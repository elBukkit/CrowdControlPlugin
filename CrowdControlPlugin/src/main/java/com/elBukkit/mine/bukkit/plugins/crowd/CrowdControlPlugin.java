package com.elBukkit.mine.bukkit.plugins.crowd;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * CrowdControl plugin
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdControlPlugin extends JavaPlugin {

	private CrowdEntityListener entityListener = new CrowdEntityListener(this);
	private PluginDescriptionFile pdf;

	public RuleHandler ruleHandler = new RuleHandler();

	public void onDisable() {
		System.out.println(pdf.getFullName() + " is disabled!");

	}

	public void onEnable() {
		pdf = this.getDescription();
		System.out.println(pdf.getFullName() + " is enabled!");

		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest,
				this);
	}
}
