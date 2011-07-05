package com.elbukkit.plugins.crowd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import com.elbukkit.plugins.crowd.events.CrowdListener;
import com.elbukkit.plugins.crowd.rules.Rule;
import com.elbukkit.plugins.crowd.rules.Type;
import com.elbukkit.plugins.crowd.utils.ThreadSafe;

/**
 * Handles all of the rules and checks if the triggering creatures passes
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class RuleHandler {

    private List<Rule> rules;

    public RuleHandler() {
        rules = new ArrayList<Rule>();
        
    }

    public void AddRule(Rule rule) {
        rules.add(rule);
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    @ThreadSafe
    public boolean passesRules(Info info, Type type) {
        Iterator<Rule> i = rules.iterator();
        while (i.hasNext()) {
            Rule r = i.next();
            if (r.getType().equals(type)) {
                if (r.checkWorld(info.getLocation().getWorld())) {
                    if (r.checkCreatureType(info.getType())) {
                        if (!r.check(info)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void rebuildDB() {

    }

    public void RemoveRule(int id) {
        rules.remove(id);
    }
}
