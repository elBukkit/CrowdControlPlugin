package com.elmakers.mine.bukkit.plugins.crowd;

import java.util.HashSet;
import java.util.Set;

import com.elmakers.mine.bukkit.plugins.crowd.rules.Rule;

public class RuleHandler {
	
	private Set<Rule> rules;
	
	public RuleHandler() {
		rules = new HashSet<Rule>();
		
		// Do loading of saved rules
	}
	
	public void AddRule(Rule rule) {
		this.rules.add(rule);
		
		// Do persistance
	}
	
	public boolean RemoveRule(Rule rule) {
		if (rules.contains(rule)) {
			rules.remove(rule);
			return true;
			
			// Remove rule from persistance
		}
		return false;
	}
	
	public boolean passesRules(SpawnInfo info) {
		for(Rule r : rules){
			if (r.checkWorld(info.getLocation().getWorld())) {
				if(r.checkCreatureType(info.getType()))
				{
					if(r.spawn(info)) {
						return true;
					}
					return false;
				}
			}
		}
		return true;
	}
}
