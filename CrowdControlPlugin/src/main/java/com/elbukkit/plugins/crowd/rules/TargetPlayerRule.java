package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * A rule that prevents creatures from targeting certain players.
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class TargetPlayerRule extends Rule {

    private String player;
    private boolean targetable;

    public TargetPlayerRule(String name, World world, CreatureType type, CrowdControlPlugin plugin) {
        super(name, world, type, plugin);
        this.ruleType = Type.Target;
    }

    @Override
    public boolean check(Info info) {
        if (!targetable) {
            if (info.getTarget() instanceof Player) {
                Player pTarget = (Player) info.getTarget();
                if (player.equalsIgnoreCase(pTarget.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void loadFromString(String data) {
        String[] split = data.split(",");
        this.player = split[0];
        this.targetable = Boolean.valueOf(split[1]);
    }

    public void save(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

    public void load(Configuration config, String node) {
        // TODO Auto-generated method stub
        
    }

}
