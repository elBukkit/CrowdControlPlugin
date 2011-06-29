package com.elbukkit.plugins.crowd;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

import com.elbukkit.plugins.crowd.creature.CreatureHandler;
import com.elbukkit.plugins.crowd.creature.CrowdCreature;

/**
 * A basic listener to check for chunk unloading and despawn any
 * {@link CrowdCreature}
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class CrowdWorldListener extends WorldListener {

    private CrowdControlPlugin plugin;

    public CrowdWorldListener(CrowdControlPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onChunkUnload(ChunkUnloadEvent event) {

        CreatureHandler handler = plugin.getCreatureHandler(event.getWorld());

        if (event.getChunk().getEntities().length > 0) {
            for (Entity e : event.getChunk().getEntities()) {
                if ((e instanceof LivingEntity) && !(e instanceof Player)) {
                    CrowdCreature c = handler.getCrowdCreature((LivingEntity) e);

                    if (c != null) {
                        handler.despawn(c);
                    }
                }
            }
        }
    }
}
