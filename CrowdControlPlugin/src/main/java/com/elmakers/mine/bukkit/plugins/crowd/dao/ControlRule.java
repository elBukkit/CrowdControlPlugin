package com.elmakers.mine.bukkit.plugins.crowd.dao;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.entity.CreatureType;

@Entity()
@Table(name = "cc_rule")
public class ControlRule
{
	@NotEmpty
    protected CreatureType creatureType;

    @NotNull
    protected float        percentChance;

    @Id
    protected int          rank;

    @NotEmpty
    protected CreatureType replaceWith;

    public ControlRule()
    {

    }

    public ControlRule(int order, CreatureType mobType)
    {
        this.rank = order;
        this.creatureType = mobType;
    }

    public CreatureType getCreatureType()
    {
        return creatureType;
    }

    public float getPercentChance()
    {
        return percentChance;
    }

    public int getRank()
    {
        return rank;
    }

    public CreatureType getReplaceWith()
    {
        return replaceWith;
    }

    public void setCreatureType(CreatureType creatureType)
    {
        this.creatureType = creatureType;
    }

    public void setPercentChance(float percentChance)
    {
        this.percentChance = percentChance;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }

    public void setReplaceWith(CreatureType replaceWith)
    {
        this.replaceWith = replaceWith;
    }
}
