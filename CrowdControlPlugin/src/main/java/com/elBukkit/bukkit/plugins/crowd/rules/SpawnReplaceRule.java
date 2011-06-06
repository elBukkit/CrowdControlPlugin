package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.Info;

public class SpawnReplaceRule extends Rule {
	
	CreatureType replaceType;

	public SpawnReplaceRule(Set<World> worlds, CreatureType type) {
		super(worlds, type);
		this.ruleType = Type.Spawn;
	}
	
	@Override
	public void init(String data)
	{
		replaceType = CreatureType.fromName(data);
	}
	
	public void init(CreatureType type)
	{
		replaceType = type;
	}
	
	@Override
	public boolean check(Info info)
	{
		info.setType(replaceType);
		return true;
	}

	@Override
	public String getData() {
		return replaceType.name();
	}

}
