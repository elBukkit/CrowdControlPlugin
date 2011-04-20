package com.elmakers.mine.bukkit.plugins.crowd.dao;

import java.util.List;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "cc_world")
public class ControlledWorld
{
	@Id
    protected WorldData         id;

	@NotNull
	@Embedded
    protected List<ControlRule> rules;

    public ControlledWorld()
    {

    }

    public ControlledWorld(WorldData world)
    {
        this.id = world;
    }

    public WorldData getId()
    {
        return id;
    }

    public List<ControlRule> getRules()
    {
        return rules;
    }

    public void setId(WorldData id)
    {
        this.id = id;
    }

    public void setRules(List<ControlRule> rules)
    {
        this.rules = rules;
    }
}
