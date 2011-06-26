package com.elBukkit.plugins.crowd.creature;

import java.util.Iterator;

import org.bukkit.Location;

import com.elBukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.plugins.crowd.Info;
import com.elBukkit.plugins.crowd.events.CreatureMoveEvent;
import com.elBukkit.plugins.crowd.events.CrowdListener;
import com.elBukkit.plugins.crowd.rules.Type;

/*
 * This handler is what detects creature movements
 * 
 * @author Andrew Querol(winsock)
 */

public class MovementHandler implements Runnable {

	private CreatureHandler handler;

	CrowdControlPlugin plugin;

	public MovementHandler(CrowdControlPlugin plugin, CreatureHandler handler) {
		this.plugin = plugin;
		this.handler = handler;
	}

	public void run() {
		Iterator<CrowdCreature> i = handler.getCrowdCreatures().iterator();

		while (i.hasNext()) {
			CrowdCreature c = i.next();

			Location lLoc = c.getLastLocation();
			Location cLoc = c.getCurrentLocation();

			if (cLoc.getBlockX() != lLoc.getBlockX() || cLoc.getBlockY() != lLoc.getBlockY() || cLoc.getBlockZ() != lLoc.getBlockZ() || cLoc.getWorld() != lLoc.getWorld()) {
				Info info = new Info();
				info.setLocation(cLoc);
				info.setEntity(c.getEntity());
				info.setEnv(c.getEntity().getWorld().getEnvironment());
				info.setType(c.getType());

				if (plugin.ruleHandler.passesRules(info, Type.Movement)) {

					CreatureMoveEvent event = new CreatureMoveEvent(this, lLoc, cLoc, c);
					for (CrowdListener cListener : plugin.getListeners()) {
						cListener.onCreatureMove(event);
					}

					if (event.isCancelled()) {
						c.getEntity().teleport(lLoc);
					} else {
						if (event.getNewLocation() != cLoc) {
							c.setLocation(event.getNewLocation());
						}
						c.setIdleTicks(0);
					}
				} else {
					c.getEntity().teleport(lLoc);
				}
			} else {
				c.setIdleTicks(c.getIdleTicks() + 1);
			}
		}

	}

}
