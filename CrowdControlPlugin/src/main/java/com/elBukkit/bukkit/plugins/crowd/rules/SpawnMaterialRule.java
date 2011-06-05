package com.elBukkit.bukkit.plugins.crowd.rules;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.SpawnInfo;

/*
 * A rule that controls spawning based on the material it spawns on.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnMaterialRule implements SpawnRule {

	private Set<World> worlds;
	private CreatureType type;

	private Set<Material> materials;

	public SpawnMaterialRule(Set<Material> materials, Set<World> worlds,
			CreatureType type) {
		this.worlds = worlds;
		this.type = type;

		this.materials = materials;
	}

	public boolean spawn(SpawnInfo info) {
		if (materials.contains(info.getLocation().getBlock().getType())) {
			return true;
		}
		return false;
	}

	public boolean checkWorld(World world) {
		if (worlds.contains(world)) {
			return true;
		}
		return false;
	}

	public boolean checkCreatureType(CreatureType type) {
		if (this.type == type) {
			return true;
		}
		return false;
	}

	public CreatureType getCreatureType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<World> getWorlds() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
