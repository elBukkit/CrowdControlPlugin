package com.elmakers.mine.bukkit.plugins.crowd;

import java.util.HashSet;
import java.util.Set;

import com.elmakers.mine.bukkit.plugins.crowd.rules.SpawnRule;
import com.elmakers.mine.bukkit.plugins.crowd.rules.TargetRule;

/*
 * Handles all of the rules and checks if the triggering creatures passes
 * 
 * @author Andrew Querol(WinSock)
 */

public class RuleHandler {

	private Set<SpawnRule> spawnRules;
	private Set<TargetRule> targetRules;
	// TODO Add when entity movement events are added, feature request #157
	// private Set<SpawnRule> movmentRules;

	public RuleHandler() {
		spawnRules = new HashSet<SpawnRule>();

		// Do loading of saved rules
	}

	public void AddRule(SpawnRule rule) {
		this.spawnRules.add(rule);

		// Do persistence
	}

	public boolean RemoveRule(SpawnRule rule) {
		if (spawnRules.contains(rule)) {
			spawnRules.remove(rule);
			return true;

			// Remove rule from persistence
		}
		return false;
	}
	
	public void AddRule(TargetRule rule) {
		this.targetRules.add(rule);

		// Do persistence
	}

	public boolean RemoveRule(TargetRule rule) {
		if (targetRules.contains(rule)) {
			targetRules.remove(rule);
			return true;

			// Remove rule from persistence
		}
		return false;
	}

	public boolean passesRules(SpawnInfo info) {
		for (SpawnRule r : spawnRules) {
			if (r.checkWorld(info.getLocation().getWorld())) {
				if (r.checkCreatureType(info.getType())) {
					if (r.spawn(info)) {
						return true;
					}
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean passesRules(TargetInfo info) {
		for (TargetRule r : targetRules) {
			if (r.checkWorld(info.getCreature().getLocation().getWorld())) {
				if (r.checkCreatureType(info.getcType())) {
					if (r.target(info)) {
						return true;
					}
					return false;
				}
			}
		}
		return true;
	}
}
