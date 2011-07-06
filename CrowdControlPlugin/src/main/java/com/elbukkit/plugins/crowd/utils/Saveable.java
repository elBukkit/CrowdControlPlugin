package com.elbukkit.plugins.crowd.utils;

import org.bukkit.util.config.Configuration;

/**
 * A simple interface for saving rules
 * 
 * @author WinSock
 * @version 1.0
 */
public interface Saveable {
    /**
     * A method to load the data
     * 
     * @param config
     *            The YAML file
     * @param node
     *            The base node
     */
    void load(Configuration config, String node);

    /**
     * A method to save the data.
     * 
     * @param config
     *            The YAML file
     * @param node
     *            The base node
     */
    void save(Configuration config, String node);
}
