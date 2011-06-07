package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.Info;

/*
 * A rule that controls spawning based on the material it spawns on.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnMaterialRule extends Rule {

	private Set<Material> materials;

	public SpawnMaterialRule(World world, CreatureType type) {
		super(world, type);
		this.ruleType = Type.Spawn;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(String data) {
		// TODO Finish init()
	}

	@Override
	public boolean check(Info info) {
		if (materials.contains(info.getLocation().getBlock().getType())) {
			return true;
		}
		return false;
	}

	@Override
	public String getData() {
		String data = "";
		for (Material m : materials) {
			data += m.name() + ",";
		}
		return data;
	}

}
