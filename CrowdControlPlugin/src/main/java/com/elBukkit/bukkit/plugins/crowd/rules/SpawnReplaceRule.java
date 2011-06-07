package com.elBukkit.bukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.Info;

public class SpawnReplaceRule extends Rule {

	CreatureType replaceType;

	public SpawnReplaceRule(World world, CreatureType type) {
		super(world, type);
		this.ruleType = Type.Spawn;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(String data) {
		replaceType = CreatureType.fromName(data);
	}

	public void init(CreatureType type) {
		replaceType = type;
	}

	@Override
	public boolean check(Info info) {
		info.setType(replaceType);
		return true;
	}

	@Override
	public String getData() {
		return replaceType.name();
	}

}
