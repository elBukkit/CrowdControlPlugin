package com.elmakers.mine.bukkit.plugins.crowd;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;

import com.elmakers.mine.bukkit.plugins.crowd.dao.ControlledWorld;
import com.elmakers.mine.bukkit.plugins.crowd.dao.WorldData;

public class CrowdEntityListener extends EntityListener
{
    protected Controller  controller;
    private CrowdControlPlugin plugin;

    public void initialize(final CrowdControlPlugin plugin, Controller controller)
    {
    	this.plugin = plugin;
        this.controller = controller;
    }

    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        ControlledWorld worldData = plugin.getDatabase().find(ControlledWorld.class).where().idEq(plugin.getDatabase().find(WorldData.class).where().idEq(event.getLocation().getWorld().getName()).findUnique()).findUnique();
        if (worldData == null)
        {
            return;
        }

        controller.controlSpawnEvent(worldData, event);
    }
}
