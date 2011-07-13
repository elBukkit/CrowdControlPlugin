package com.elbukkit.plugins.crowd.rules;

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
    
    private String  player;
    private boolean targetable;
    
    public TargetPlayerRule(final String name, final CreatureType type, final CrowdControlPlugin plugin) {
        super(name, type, plugin);
        this.ruleType = Type.TARGET;
    }
    
    @Override
    public boolean check(final Info info) {
        if (!this.targetable) {
            if (info.getTarget() instanceof Player) {
                final Player pTarget = (Player) info.getTarget();
                if (this.player.equalsIgnoreCase(pTarget.getName())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void load(final Configuration config, final String node) {
        this.targetable = config.getBoolean(node + ".targetable", true);
        this.player = config.getString(node + ".player", "Player");
    }
    
    @Override
    public void loadFromString(final String data) {
        final String[] split = data.split(" ");
        this.player = split[0];
        this.targetable = Boolean.valueOf(split[1]);
    }
    
    public void save(final Configuration config, final String node) {
        config.setProperty(node + ".player", this.player);
        config.setProperty(node + ".targetable", this.targetable);
    }
    
}
