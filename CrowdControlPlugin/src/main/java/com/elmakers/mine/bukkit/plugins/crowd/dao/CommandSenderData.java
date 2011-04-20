package com.elmakers.mine.bukkit.plugins.crowd.dao;

import com.avaje.ebean.validation.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a possible command sender.
 * 
 * This entity is pre-populated, currently only "generic" and "player" present.
 * 
 * Use of this class is currently hard-coded, so it would not be advised to add
 * or modify this data.
 * 
 * @author nathan
 * 
 */
@Entity()
@Table(name = "cc_sender")
public class CommandSenderData
{
	@NotEmpty
    protected String className;

    @Id
    protected String id;

    public CommandSenderData()
    {

    }

    public CommandSenderData(String id, Class<?> senderClass)
    {
        this.id = id;
        if (senderClass != null)
        {
            this.className = senderClass.getName();
        }
    }
    
    public String getClassName()
    {
        return className;
    }

    public String getId()
    {
        return id;
    }

    public Class<?> getType()
    {
        if (className == null || className.length() == 0)
        {
            return null;
        }
        Class<?> senderType = null;
        try
        {
            senderType = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            senderType = null;
        }
        return senderType;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}