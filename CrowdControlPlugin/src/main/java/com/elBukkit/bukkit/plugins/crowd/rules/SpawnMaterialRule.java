package com.elBukkit.bukkit.plugins.crowd.rules;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * A rule that controls spawning based on the material it spawns on.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnMaterialRule extends Rule {

	private Material material;
	private boolean spawnable = true;

	public SpawnMaterialRule(World world, CreatureType type,
			CrowdControlPlugin plugin) {
		super(world, type, plugin);
		this.ruleType = Type.Spawn;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(String data) {
		String[] split = data.split(",");
		material = Material.valueOf(split[0]);
		spawnable = Boolean.valueOf(split[1]);
	}

	@Override
	public boolean check(Info info) {
		if (material.equals(info.getLocation().getBlock().getType())) {
			return true;
		}
		return false;
	}

	@Override
	public String getData() {
		return material.toString() + "," + String.valueOf(spawnable);
	}

}
