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

    private Configuration config;
    private File configFile;
    private ConcurrentHashMap<Entry<String, Entry<Class<? extends Rule>, CreatureType>>, Rule> rules;
    private World world;

    public RuleHandler(World world, CrowdControlPlugin plugin) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        this.world = world;
        rules = new ConcurrentHashMap<Entry<String, Entry<Class<? extends Rule>, CreatureType>>, Rule>();
        configFile = new File(plugin.getDataFolder() + File.separator + world.getName() + ".yml");
        if (!configFile.exists()) {
            File defaults = new File(plugin.getDataFolder() + File.separator + world.getEnvironment().toString() + ".yml");
            if (defaults.exists()) {
                FileUtils.copyFile(defaults, configFile);
            } else {
                configFile.createNewFile();
            }
        }
        config = new Configuration(configFile);
        config.load();
        List<String> nodes = config.getKeys("rules");
        if (nodes != null) {
            for (String node : nodes) {
                List<String> classes = config.getKeys("rules." + node);
                if (classes != null) {
                    for (String ruleC : classes) {
                        // ESCA-JAVA0179:
                        List<String> rules = config.getKeys("rules." + node + "." + ruleC);
                        if (rules != null) {
                            for (String ruleName : rules) {
                                Class<? extends Rule> ruleClass = Class.forName("com.elbukkit.plugins.crowd.rules." + ruleC).asSubclass(Rule.class);
                                Constructor<? extends Rule> c = ruleClass.getDeclaredConstructor(String.class, CreatureType.class, CrowdControlPlugin.class);
                                Rule r = c.newInstance(ruleName, CreatureType.valueOf(node.toUpperCase()), plugin);
                                r.load(config, "rules." + r.getCreatureType() + "." + ruleClass.getSimpleName() + "." + r.getName());
                                this.rules.put(new AbstractMap.SimpleEntry<String, Entry<Class<? extends Rule>, CreatureType>>(r.getName(), new AbstractMap.SimpleEntry<Class<? extends Rule>, CreatureType>(ruleClass, r.getCreatureType())), r);
                            }
                        }
                    }
                }
            }
        }
    }

    public void AddRule(Rule rule) {
        this.rules.put(new AbstractMap.SimpleEntry<String, Entry<Class<? extends Rule>, CreatureType>>(rule.getName(), new AbstractMap.SimpleEntry<Class<? extends Rule>, CreatureType>(rule.getClass(), rule.getCreatureType())), rule);

        config.load();
        rule.save(config, "rules." + rule.getCreatureType() + "." + rule.getClass().getSimpleName() + "." + rule.getName());
        config.save();
    }

    public Collection<Rule> getRules() {
        return Collections.unmodifiableCollection(rules.values());
    }

    public World getWorld() {
        return world;
    }

    @ThreadSafe
    public boolean passesRules(Info info, Type type) {
        Iterator<Rule> i = rules.values().iterator();
        while (i.hasNext()) {
            Rule r = i.next();
            if (r.getType().equals(type)) {
                if (r.checkCreatureType(info.getType())) {
                    if (!r.check(info)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void RemoveRule(Entry<String, Entry<Class<? extends Rule>, CreatureType>> entry) {
        Rule r = rules.get(entry);
        config.removeProperty("rules." + r.getCreatureType() + "." + entry.getValue().getKey().getSimpleName() + "." + r.getName());
        rules.remove(entry);
        config.save();
    }
}
