package com.elBukkit.bukkit.plugins.crowd.creature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Spawn implements Runnable {
	
	Random generator = new Random();

	public void run() {
		Set<Player> players = new HashSet<Player>(Arrays.asList(Bukkit.getServer().getOnlinePlayers()));

		for(Player p : players) {
			
			List<Chunk> chunks = new ArrayList<Chunk>();
			World world = p.getWorld();
			Chunk c = world.getChunkAt(p.getLocation());
			
			for (int x = 0; x < c.getX() - 8; x++) {
				for (int z = 0; z < c.getZ() + 8; z++){
					chunks.add(world.getChunkAt(x, z));
				}
			}
			
			Collections.shuffle(chunks); // Randomize chunk order
			
			boolean cancel = false;
			
			while(chunks.size() > 0 && !cancel)
			{
				if (generator.nextInt(10) == 1) {
					Chunk spawnChunk = chunks.remove(0);
					Block packBlock = spawnChunk.getBlock(generator.nextInt(15), generator.nextInt(127), generator.nextInt(15));
					
					if (spawnChunk.getBlock(packBlock.getX(), packBlock.getY() + 1, packBlock.getZ()).getType() == Material.AIR)
					{
						if (spawnChunk.getBlock(packBlock.getX(), packBlock.getY() + 2, packBlock.getZ()).getType() == Material.AIR)
						{
							Set<Block> spawnBlocks = new HashSet<Block>();
							
							
							for (int x = 0; x < packBlock.getX() - 1; x++){
								for (int z = 0; z < packBlock.getZ() + 1; z++){
									while(spawnBlocks.size() < 6)
									{
										if (spawnChunk.getBlock(x, packBlock.getY() + 1, z).getType() == Material.AIR)
										{
											if (spawnChunk.getBlock(x, packBlock.getY() + 2, z).getType() == Material.AIR)
											{
												spawnBlocks.add(world.getBlockAt(x, packBlock.getY(), z));
											}
										}
									}
								}
							}
							
							// Passes all checks go to individual spawning code
							// TODO Individual spawning code
							
							System.out.println("CrowdControl: spawner test");
							
						}
					}
					
					cancel = true; // Spawn block is in a solid area bail out
				}
				else
				{
					chunks.remove(0);
				}
			}
		}
		
	}

}
