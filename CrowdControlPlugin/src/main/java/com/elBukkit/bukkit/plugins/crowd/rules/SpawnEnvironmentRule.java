package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * A rule that allows or disallows creatures based on the environment, Normal, Nether, (Soon) Ather
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnEnvironmentRule extends Rule {

	private Set<Environment> spawnableEnvironment;

	public SpawnEnvironmentRule(Set<World> worlds, CreatureType type) {
		super(worlds, type);
		this.ruleType = Type.Spawn;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(String data) {
		// TODO Finish init()
	}

	@Override
	public boolean check(Info info) {
		if (this.spawnableEnvironment.contains(info.getEnv())) {
			return true;
		}
		return false;
	}

	@Override
	public String getData() {
		// TODO Finish getData()
		return null;
	}
}
