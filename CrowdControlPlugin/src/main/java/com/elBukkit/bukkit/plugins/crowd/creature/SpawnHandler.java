package com.elBukkit.bukkit.plugins.crowd.creature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import com.elBukkit.bukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.bukkit.plugins.crowd.Info;
import com.elBukkit.bukkit.plugins.crowd.rules.Type;

public class SpawnHandler implements Runnable {

	private CreatureHandler handler;
	private CrowdControlPlugin plugin;
	private Random rand = new Random();
	private World world;

	public SpawnHandler(CrowdControlPlugin plugin, World world, CreatureHandler handler) {
		this.plugin = plugin;
		this.world = world;
		this.handler = handler;
	}

	private Block getRandomSpawningPointInChunk(Chunk c) {
		int x = c.getX() + rand.nextInt(16);
		int y = rand.nextInt(128);
		int z = c.getZ() + rand.nextInt(16);
		return c.getBlock(x, y, z);
	}

	public void run() {

		if (handler.getEnabledCreatureTypes().size() > 0) {

			List<Chunk> spawningChunks = new ArrayList<Chunk>();

			for (Player p : world.getPlayers()) {

				int pChunkX = (int) Math.floor(p.getLocation().getX() / 16.0D);
				int pChunkZ = (int) Math.floor(p.getLocation().getZ() / 16.0D);

				for (int x = -8; x <= 8; x++) {
					for (int z = -8; z <= 8; z++) {
						spawningChunks.add(world.getChunkAt(x + pChunkX, z + pChunkZ));
					}
				}
			}

			Collections.shuffle(spawningChunks, rand); // Randomize the list

			for (int i = 0; i <= 3; i++) { // 4 chances to spawn

				for (Chunk c : spawningChunks) {
					if (handler.getCreatureCount() < plugin.getMaxPerWorld()) {
						if (c.getEntities().length < plugin.getMaxPerChunk()) {

							List<CreatureType> enabledTypes = handler.getEnabledCreatureTypes();
							CreatureType type = enabledTypes.get(rand.nextInt(enabledTypes.size()));

							Block spawnBlock = getRandomSpawningPointInChunk(c);

							if (spawnBlock.getType() == Material.AIR || spawnBlock.getType() == Material.WATER || spawnBlock.getType() == Material.STATIONARY_WATER) {
								if (world.getBlockAt(spawnBlock.getX(), spawnBlock.getY() - 1, spawnBlock.getZ()).getType() != Material.AIR) {
									Info info = new Info();

									info.setLocation(spawnBlock.getLocation());
									info.setEnv(world.getEnvironment());
									info.setType(type);

									if (rand.nextFloat() < handler.getInfo(type).getSpawnChance()) {

										if (plugin.ruleHandler.passesRules(info, Type.Spawn)) {
											info.spawn();
											plugin.getCreatureHandler(spawnBlock.getLocation().getWorld()).addLivingEntity(info.getEntity());
										}

									}
								}
							}
						}
					}
				}
			}
		}

	}

}