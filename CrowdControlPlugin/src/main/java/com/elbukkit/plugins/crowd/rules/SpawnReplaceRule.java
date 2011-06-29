package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/**
 * A rule that replaces a creature type with another
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class SpawnReplaceRule extends Rule {

    private CreatureType replaceType;

    public SpawnReplaceRule(World world, CreatureType type, CrowdControlPlugin plugin) {
        super(world, type, plugin);
        this.ruleType = Type.Spawn;
    }

    @Override
    public boolean check(Info info) {
        info.setType(replaceType);
        return true;
    }

    @Override
    public String getData() {
        return replaceType.toString();
    }

    public void init(CreatureType cType) {
        replaceType = cType;
    }

    @Override
    public void init(String data) {
        replaceType = CreatureType.valueOf(data);
    }

}
