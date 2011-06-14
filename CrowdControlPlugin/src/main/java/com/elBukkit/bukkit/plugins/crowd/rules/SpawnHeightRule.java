package com.elBukkit.bukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * A rule that controls spawning based on height.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnHeightRule extends Rule {

    int min,
            max;

    public SpawnHeightRule(World world, CreatureType type, CrowdControlPlugin plugin) {
        super(world, type, plugin);
        this.ruleType = Type.Spawn;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init(String data) {
        String[] split = data.split(",");
        this.max = Integer.parseInt(split[0]);
        this.min = Integer.parseInt(split[1]);
    }

    public boolean spawn(Info info) {
        if (info.getLocation().getBlockY() >= min) {
            if (info.getLocation().getBlockY() <= max) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getData() {
        return String.valueOf(max) + "," + String.valueOf(min);
    }

}
