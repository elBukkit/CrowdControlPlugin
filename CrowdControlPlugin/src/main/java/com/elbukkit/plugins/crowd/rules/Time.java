package com.elbukkit.plugins.crowd.rules;

/**
 * a simple enum for the time of the day.
 * 
 * @author Andrew Querol(winsock)
 * @version 1.0
 */
public enum Time {
    DAY, NIGHT;
    
    public static Time findTime(String s) throws IllegalArgumentException {
        for (Time t : Time.values()) {
            if (t.name().equals(s.toUpperCase())) {
                return t;
            } else if (t.name().replaceAll("_", "").equals(s.toUpperCase())) {
                return t;
            }
        }
        
        throw new IllegalArgumentException("Unknown time : " + s);
    }
}
