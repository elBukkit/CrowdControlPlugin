package com.elBukkit.plugins.crowd.rules;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.plugins.crowd.Info;

/*
 * A rule that controls spawning based on the material it spawns on.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnMaterialRule extends Rule {

	private Material material;

	public SpawnMaterialRule(World world, CreatureType type, CrowdControlPlugin plugin) {
		super(world, type, plugin);
		this.ruleType = Type.Spawn;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean check(Info info) {
		Material blockMaterial = world.getBlockAt(info.getLocation().getBlockX(), info.getLocation().getBlockY() - 1, info.getLocation().getBlockZ()).getType();
		Material spawnBlockMaterial = world.getBlockAt(info.getLocation().getBlockX(), info.getLocation().getBlockY(), info.getLocation().getBlockZ()).getType();
		if (material != blockMaterial && material != spawnBlockMaterial) {
			return true;
		}
		return false;
	}

	@Override
	public String getData() {
		return material.toString();
	}

	@Override
	public void init(String data) {
		String[] split = data.split(",");
		material = Material.valueOf(split[0]);
	}

}