package com.elBukkit.bukkit.plugins.crowd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;

import com.elBukkit.bukkit.plugins.crowd.rules.Rule;

/*
 * Handles all of the commands
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdCommand implements CommandExecutor {

	CrowdControlPlugin plugin;
	List<Class<? extends Rule>> pendingCommands = new ArrayList<Class<? extends Rule>>();

	public CrowdCommand(CrowdControlPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if (args.length < 1) {
			sender.sendMessage("Error not enough arguments");
			return false;
		}

		if (args[0].equalsIgnoreCase("add")) {
			if (!(args.length < 2)) {
				for (Class<? extends Rule> c : plugin.commands.keySet()) {
					if (args[1].equals(c.getSimpleName())) {
						pendingCommands.add(c);
						sender.sendMessage("Add added to pending with id: "
								+ String.valueOf(pendingCommands.indexOf(c)));
						sender.sendMessage("Args needed: ");
						sender.sendMessage("Use crowd finish "
								+ pendingCommands.indexOf(c)
								+ " [worldname] [creaturetype] "
								+ plugin.commands.get(c) + " to complete");
						return true;
					}
				}
			} else {
				sender.sendMessage("Usage: crowd add [Rule class]");
				return false;
			}
		} else if (args[0].equalsIgnoreCase("listPending")) {
			for (Class<? extends Rule> c : pendingCommands) {
				sender.sendMessage(c.getSimpleName() + ", ID: "
						+ String.valueOf(pendingCommands.indexOf(c)));
				sender.sendMessage("[data] = " + plugin.commands.get(c));
			}
			return true;
		} else if (args[0].equalsIgnoreCase("finish")) {
			if (args.length >= 5) {
				if (pendingCommands.size() > 0) {
					if (pendingCommands.size() >= Integer.valueOf(args[1])) {

						Class<? extends Rule> ruleClass = pendingCommands
								.get(Integer.valueOf(args[1]));
						Constructor<? extends Rule> c;
						try {
							c = ruleClass.getDeclaredConstructor(World.class,
									CreatureType.class);
							Object classObj = c.newInstance(Bukkit.getServer()
									.getWorld(args[2]), CreatureType
									.valueOf(args[3]));
							if (classObj instanceof Rule) {
								Rule r = (Rule) classObj;
								r.init(args[4]);
								plugin.ruleHandler.AddRule(r);
								sender.sendMessage("Rule added!");
								pendingCommands.remove(ruleClass);
							}
						} catch (SecurityException e) {
							sender.sendMessage("Error: security exception!");
							return false;
						} catch (NoSuchMethodException e) {
							sender.sendMessage("Error: no such method");
							return false;
						} catch (IllegalArgumentException e) {
							sender.sendMessage("Error: argument exception! Are the arguments in the right order?");
							return false;
						} catch (InstantiationException e) {
							sender.sendMessage("Error: Failed to create a new rule instance!");
							return false;
						} catch (IllegalAccessException e) {
							sender.sendMessage("Error: Illegal Access!");
							return false;
						} catch (InvocationTargetException e) {
							sender.sendMessage("Error: Invocation Exception");
							return false;
						} catch (SQLException e) {
							sender.sendMessage("Error: failed to save rule to disk, rule is still in memory. Call \"crowd rebuildDB\" to try to fix");
							return false;
						}
						return true;
					} else {
						sender.sendMessage("Invalid pending command ID, use /crowd listPending to get a list of pending commands");
						return false;
					}
				} else {
					sender.sendMessage("No pending commands to add!");
					return false;
				}
			} else {
				sender.sendMessage("usage: crowd finish [pending id] [worldname] [creaturetype] [data] ");
				return false;
			}
		} else if (args[0].equalsIgnoreCase("listRules")) {
			if (plugin.commands.size() > 0) {
				String ruleList = "";
				for (Class<? extends Rule> r : plugin.commands.keySet()) {
					if (ruleList.length() > 0) {
						ruleList += ", ";
					}
					ruleList += r.getSimpleName();
				}
				sender.sendMessage("Available Rules:");
				sender.sendMessage(ruleList);
				return true;
			} else {
				sender.sendMessage("No rules!"); // should never happen :)
				return false;
			}
		} else if (args[0].equalsIgnoreCase("rebuildDB")) {
			try {
				plugin.ruleHandler.rebuildDB();
				sender.sendMessage("Database is rebuilt! All rules were re-added");
				return true;
			} catch (SQLException e) {
				sender.sendMessage("Error rebuilding database!");
				return false;
			}
		} else if (args[0].equalsIgnoreCase("listEnabledRules")) {
			Map<Integer, Rule> rules = plugin.ruleHandler.getRules();
			for (int i : rules.keySet()) {
				sender.sendMessage(((Object) rules.get(i)).getClass()
						.getSimpleName() + ", ID: " + i);
			}
			return true;
		} else if (args[0].equalsIgnoreCase("getDetailedInfo")) {
			if (args.length >= 2) {
				Rule r = plugin.ruleHandler.getRules().get(
						Integer.valueOf(args[1]));
				sender.sendMessage("Creature Type: "
						+ r.getCreatureType().toString());
				sender.sendMessage("World: " + r.getWorld().getName());
				sender.sendMessage("Data: " + r.getData());
				return true;
			} else {
				sender.sendMessage("Usage: crowd getDetailedInfo [enabled id]");
				return false;
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (args.length >= 2) {
				plugin.ruleHandler.RemoveRule(Integer.valueOf(args[1]));
				try {
					plugin.ruleHandler.rebuildDB();
				} catch (SQLException e) {
					sender.sendMessage("Error rebuilding database!");
				}
				sender.sendMessage("Removed rule with id: " + args[1] + "!");
				return true;
			} else {
				sender.sendMessage("Usage: crowd remove [enabled id]");
				return false;
			}
		}

		return true;
	}

}
