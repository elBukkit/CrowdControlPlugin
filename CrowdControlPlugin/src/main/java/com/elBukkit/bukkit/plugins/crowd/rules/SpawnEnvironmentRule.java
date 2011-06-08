package com.elBukkit.bukkit.plugins.crowd.rules;

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

	private Environment spawnableEnvironment;

	public SpawnEnvironmentRule(World world, CreatureType type) {
		super(world, type);
		this.ruleType = Type.Spawn;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(String data) {
		this.spawnableEnvironment = Environment.valueOf(data);
	}

	@Override
	public boolean check(Info info) {
		if (this.spawnableEnvironment.equals(info.getEnv())) {
			return true;
		}
		return false;
	}

	@Override
	public String getData() {
		return spawnableEnvironment.toString();
	}
}
