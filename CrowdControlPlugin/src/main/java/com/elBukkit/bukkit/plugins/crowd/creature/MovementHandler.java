package com.elBukkit.bukkit.plugins.crowd.creature;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Location;

import com.elBukkit.bukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.bukkit.plugins.crowd.Info;
import com.elBukkit.bukkit.plugins.crowd.events.CreatureMoveEvent;
import com.elBukkit.bukkit.plugins.crowd.events.CrowdListener;
import com.elBukkit.bukkit.plugins.crowd.rules.Type;

public class MovementHandler implements Runnable {

	CreatureHandler handler;
	Map<CrowdCreature, Location> lastLocation = new HashMap<CrowdCreature, Location>();

	CrowdControlPlugin plugin;

	public MovementHandler(CrowdControlPlugin plugin, CreatureHandler handler) {
		this.plugin = plugin;
		this.handler = handler;
	}

	public void run() {
		Iterator<CrowdCreature> i = handler.getCrowdCreatures().iterator();

		while (i.hasNext()) {
			CrowdCreature c = i.next();

			if (lastLocation.containsKey(c)) {
				Location loc = lastLocation.get(c);
				Location cLoc = c.getEntity().getLocation();

				if (cLoc.getBlockX() != loc.getBlockX() || cLoc.getBlockY() != loc.getBlockY() || cLoc.getBlockZ() != loc.getBlockZ() || cLoc.getWorld() != loc.getWorld()) {
					Info info = new Info();
					info.setLocation(cLoc);
					info.setEntity(c.getEntity());
					info.setEnv(c.getEntity().getWorld().getEnvironment());
					info.setType(c.getType());

					if (plugin.ruleHandler.passesRules(info, Type.Movement)) {

						CreatureMoveEvent event = new CreatureMoveEvent(this, loc, cLoc, c);
						for (CrowdListener cListener : plugin.getListeners()) {
							cListener.onCreatureMove(event);
						}

						if (event.isCancelled()) {
							c.getEntity().teleport(loc);
						} else {
							if (event.getNewLocation() != cLoc) {
								c.getEntity().teleport(event.getNewLocation());
							}
							lastLocation.put(c, event.getNewLocation());
						}
					} else {
						c.getEntity().teleport(loc);
					}
				}

			} else {
				lastLocation.put(c, c.getEntity().getLocation().clone());
			}
		}

	}

}
