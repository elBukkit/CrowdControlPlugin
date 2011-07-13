package com.elbukkit.plugins.crowd;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import com.elbukkit.plugins.crowd.rules.Rule;
import com.elbukkit.plugins.crowd.rules.Type;
import com.elbukkit.plugins.crowd.utils.FileUtils;
import com.elbukkit.plugins.crowd.utils.ThreadSafe;

/**
 * Handles all of the rules and checks if the triggering creatures passes
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class RuleHandler {
    
    private final Configuration                                                                      config;
    private final File                                                                               configFile;
    private final ConcurrentHashMap<Entry<String, Entry<Class<? extends Rule>, CreatureType>>, Rule> rules;
    private final World                                                                              world;
    
    public RuleHandler(final World world, final CrowdControlPlugin plugin) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        this.world = world;
        this.rules = new ConcurrentHashMap<Entry<String, Entry<Class<? extends Rule>, CreatureType>>, Rule>();
        this.configFile = new File(plugin.getDataFolder() + File.separator + world.getName() + ".yml");
        if (!this.configFile.exists()) {
            final File defaults = new File(plugin.getDataFolder() + File.separator + world.getEnvironment().toString() + ".yml");
            if (defaults.exists()) {
                FileUtils.copyFile(defaults, this.configFile);
            } else {
                if (this.configFile.createNewFile()) {
                    plugin.getLog().info("[CrowdControl] Created config for " + world.getName() + "!");
                }
            }
        }
        this.config = new Configuration(this.configFile);
        this.config.load();
        final List<String> nodes = this.config.getKeys("rules");
        if (nodes != null) {
            for (final String node : nodes) {
                final List<String> classes = this.config.getKeys("rules." + node);
                if (classes != null) {
                    for (final String ruleC : classes) {
                        // ESCA-JAVA0179:
                        final List<String> rules = this.config.getKeys("rules." + node + "." + ruleC);
                        if (rules != null) {
                            for (final String ruleName : rules) {
                                final Class<? extends Rule> ruleClass = Class.forName("com.elbukkit.plugins.crowd.rules." + ruleC).asSubclass(Rule.class);
                                final Constructor<? extends Rule> constructor = ruleClass.getDeclaredConstructor(String.class, CreatureType.class, CrowdControlPlugin.class);
                                final Rule rule = constructor.newInstance(ruleName, CreatureType.valueOf(node.toUpperCase()), plugin);
                                rule.load(this.config, "rules." + rule.getCreatureType() + "." + ruleClass.getSimpleName() + "." + rule.getName());
                                this.rules.put(new AbstractMap.SimpleEntry<String, Entry<Class<? extends Rule>, CreatureType>>(rule.getName(), new AbstractMap.SimpleEntry<Class<? extends Rule>, CreatureType>(ruleClass, rule.getCreatureType())), rule);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void addRule(final Rule rule) {
        this.rules.put(new AbstractMap.SimpleEntry<String, Entry<Class<? extends Rule>, CreatureType>>(rule.getName(), new AbstractMap.SimpleEntry<Class<? extends Rule>, CreatureType>(rule.getClass(), rule.getCreatureType())), rule);
        
        this.config.load();
        rule.save(this.config, "rules." + rule.getCreatureType() + "." + rule.getClass().getSimpleName() + "." + rule.getName());
        this.config.save();
    }
    
    public Collection<Rule> getRules() {
        return Collections.unmodifiableCollection(this.rules.values());
    }
    
    public World getWorld() {
        return this.world;
    }
    
    @ThreadSafe
    public boolean passesRules(final Info info, final Type type) {
        final Iterator<Rule> i = this.rules.values().iterator();
        while (i.hasNext()) {
            final Rule rule = i.next();
            if (rule.getType().equals(type) && rule.checkCreatureType(info.getType()) && !rule.check(info)) {
                return false;
            }
        }
        return true;
    }
    
    public void removeRule(final Entry<String, Entry<Class<? extends Rule>, CreatureType>> entry) {
        final Rule rule = this.rules.get(entry);
        this.config.removeProperty("rules." + rule.getCreatureType() + "." + entry.getValue().getKey().getSimpleName() + "." + rule.getName());
        this.rules.remove(entry);
        this.config.save();
    }
}
