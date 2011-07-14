package com.elbukkit.plugins.crowd.creature;

/**
 * A simple enum about a creatures nature or stance.
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public enum Nature {
    AGGRESSIVE, NEUTRAL, PASSIVE;
    
    public static Nature findNature(String s) throws IllegalArgumentException {
        for (Nature n : Nature.values()) {
            if (n.name().equals(s.toUpperCase())) {
                return n;
            } else if (n.name().replaceAll("_", "").equals(s.toUpperCase())) {
                return n;
            }
        }
        
        throw new IllegalArgumentException("Unknown creature nature : " + s);
    }
}
