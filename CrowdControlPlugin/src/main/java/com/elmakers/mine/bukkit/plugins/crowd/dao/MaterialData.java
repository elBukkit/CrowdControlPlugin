package com.elmakers.mine.bukkit.plugins.crowd.dao;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Encapsulates a Material class and its data value.
 * 
 * @author NathanWolf
 * 
 */

@Entity()
@Table(name = "cc_material")
public class MaterialData
{
	@NotNull
    protected short    data;
    @Id
    protected Material type;

    public MaterialData()
    {

    }

    public MaterialData(Block block)
    {
        this.type = block.getType();
        this.data = block.getData();
    }

    public MaterialData(ItemStack stack)
    {
        if (stack != null)
        {
            this.data = (byte) stack.getDurability();
            this.type = stack.getType();
        }
    }

    public MaterialData(Material mat)
    {
        this.type = mat;
        this.data = 0;
    }

    public MaterialData(Material mat, byte data)
    {
        this.type = mat;
        this.data = data;
    }
    
    public byte getData()
    {
        return (byte) data;
    }

    public short getDurability()
    {
        return data;
    }

    public Material getType()
    {
        return type;
    }
    
    public String getName()
    {
        // TODO: Support variant names
        return type.name().toLowerCase();
    }

    /**
     * Returns a hash code for this Location- does not include orientation.
     * 
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        int materialHash = type.hashCode();
        return materialHash << 8 | data & 0xFF;
    }

    public void setData(byte data)
    {
        this.data = data;
    }

    public void setDurability(short durability)
    {
        this.data = durability;
    }

    public void setType(Material type)
    {
        this.type = type;
    }
}
