package com.elmakers.mine.bukkit.plugins.crowd.dao;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.util.BlockVector;

import com.avaje.ebean.validation.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "cc_world")
public class WorldData
{
	@NotEmpty
    protected Environment environmentType;

    @Id
    protected String      name;

    @NotEmpty
    protected BlockVector spawn;

    @NotEmpty
    protected World       world;

    public WorldData()
    {

    }

    public WorldData(String name, Environment type)
    {
        this.name = name;
        setEnvironmentType(type);
    }

    public WorldData(World world)
    {
        update(world);
    }

    public Environment getEnvironmentType()
    {
        return environmentType;
    }

    public String getName()
    {
        return name;
    }

    public BlockVector getSpawn()
    {
        return spawn;
    }

    public World getWorld()
    {
        if (world != null)
        {
            return world;
        }

        Server server = Bukkit.getServer();
        if (server == null)
        {
            return null;
        }

        List<World> worlds = server.getWorlds();
        for (World checkWorld : worlds)
        {
            if (checkWorld.getName().equalsIgnoreCase(name))
            {
                this.world = checkWorld;
                return world;
            }
        }

        world = server.createWorld(name, getEnvironmentType());
        return world;
    }

    public void setEnvironmentType(Environment environmentType)
    {
        this.environmentType = environmentType;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setSpawn(BlockVector spawn)
    {
        this.spawn = spawn;
    }

    public void update(World world)
    {
        this.world = world;

        name = world.getName();
        Location location = world.getSpawnLocation();
        spawn = new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        setEnvironmentType(world.getEnvironment());
    }
}
