package com.elbukkit.plugins.crowd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;

import com.elbukkit.plugins.crowd.creature.BaseInfo;
import com.elbukkit.plugins.crowd.creature.Nature;
import com.elbukkit.plugins.crowd.rules.Rule;

/**
 * Handles all of the commands
 * 
 * @author Andrew Querol(WinSock)
 * @version 1.0
 */
public class CrowdCommand implements CommandExecutor {

    private List<Class<? extends Rule>> pendingCommands = new ArrayList<Class<? extends Rule>>();
    private CrowdControlPlugin plugin;

    public CrowdCommand(CrowdControlPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage("Must be OP of the server to use this command");
            return false;
        }

        if (args.length < 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (!(args.length < 2)) {
                for (Class<? extends Rule> c : plugin.getRules().keySet()) {
                    if (args[1].equals(c.getSimpleName())) {
                        pendingCommands.add(c);
                        sender.sendMessage("Add added to pending with id: " + String.valueOf(pendingCommands.indexOf(c)));
                        sender.sendMessage("Args needed: ");
                        sender.sendMessage("Use crowd finish " + pendingCommands.indexOf(c) + " [name] [worldname] [creaturetype] " + plugin.getRules().get(c) + " to complete");
                    }
                }
            } else {
                sender.sendMessage("Usage: crowd add [Rule class]");
            }
        } else if (args[0].equalsIgnoreCase("listPending")) {
            for (Class<? extends Rule> c : pendingCommands) {
                sender.sendMessage(c.getSimpleName() + ", ID: " + String.valueOf(pendingCommands.indexOf(c)));
                sender.sendMessage("[data] = " + plugin.getRules().get(c));
            }
            return true;
        } else if (args[0].equalsIgnoreCase("finish")) {
            if (args.length >= 5) {
                if (pendingCommands.size() > 0) {
                    if (pendingCommands.size() >= Integer.valueOf(args[1])) {

                        Class<? extends Rule> ruleClass = pendingCommands.get(Integer.valueOf(args[1]));
                        Constructor<? extends Rule> c = null;
                        try {
                            c = ruleClass.getDeclaredConstructor(String.class, CreatureType.class, CrowdControlPlugin.class);
                            Object classObj = c.newInstance(args[2], CreatureType.valueOf(args[4]), plugin);
                            if (classObj instanceof Rule) {
                                Rule r = (Rule) classObj;
                                String load = "";
                                for (int i = 5; i < args.length; i++) {
                                    load += args[i] + " ";
                                }
                                r.loadFromString(load);
                                plugin.getRuleHandler(Bukkit.getServer().getWorld(args[3])).AddRule(r);
                                sender.sendMessage("Rule added!");
                                pendingCommands.remove(ruleClass);
                            }
                        } catch (SecurityException e) {
                            sender.sendMessage("Error: security exception!");
                        } catch (NoSuchMethodException e) {
                            sender.sendMessage("Error: no such method");
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage("Error: argument exception! Are the arguments in the right order?");
                        } catch (InstantiationException e) {
                            sender.sendMessage("Error: Failed to create a new rule instance!");
                        } catch (IllegalAccessException e) {
                            sender.sendMessage("Error: Illegal Access!");
                        } catch (InvocationTargetException e) {
                            sender.sendMessage("Error: Invocation Exception");
                        }
                    } else {
                        sender.sendMessage("Invalid pending command ID, use /crowd listPending to get a list of pending commands");
                    }
                } else {
                    sender.sendMessage("No pending commands to add!");
                }
            } else {
                sender.sendMessage("usage: crowd finish [pending id] [name] [worldname] [creaturetype] [data] ");
            }
        } else if (args[0].equalsIgnoreCase("listRules")) {
            if (plugin.getRules().size() > 0) {
                String ruleList = "";
                for (Class<? extends Rule> r : plugin.getRules().keySet()) {
                    if (ruleList.length() > 0) {
                        ruleList += ", ";
                    }
                    ruleList += r.getSimpleName();
                }
                sender.sendMessage("Available Rules:");
                sender.sendMessage(ruleList);
            } else {
                sender.sendMessage("No rules!"); // should never happen :)
            }
        } else if (args[0].equalsIgnoreCase("listEnabledRules")) {
            if (args.length > 1) {
                Set<Rule> rules = new HashSet<Rule>(plugin.getRuleHandler(Bukkit.getServer().getWorld(args[1])).getRules());
                Iterator<Rule> i = rules.iterator();

                while (i.hasNext()) {
                    Rule r = i.next();
                    sender.sendMessage(r.getCreatureType().toString() + ", " + r.getClass().getSimpleName() + ", Name: " + r.getName());
                }
            } else {
                sender.sendMessage("Usage: crowd listEnabledRules [world]");
            }
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length > 3) {
                try {
                    plugin.getRuleHandler(Bukkit.getServer().getWorld(args[1])).RemoveRule(new AbstractMap.SimpleEntry<String, Entry<Class<? extends Rule>, CreatureType>>(args[2], new AbstractMap.SimpleEntry<Class<? extends Rule>, CreatureType>(Class.forName("com.elbukkit.plugins.crowd.rules." + args[3]).asSubclass(Rule.class), CreatureType.valueOf(args[4].toUpperCase()))));
                } catch (ClassNotFoundException e) {
                    sender.sendMessage("Unable to remove the rule!");
                }
                sender.sendMessage("Removed rule with name: " + args[2] + "!");
            } else {
                sender.sendMessage("Usage: crowd remove [world] [name] [rule class] [creature type]");
            }
        } else if (args[0].equalsIgnoreCase("removePending")) {
            if (args.length >= 2) {
                pendingCommands.remove(Integer.parseInt(args[1]));
                sender.sendMessage("Removed pending rule with ID: " + args[1]);
            } else {
                sender.sendMessage("Usage: crowd removePending [pending id]");
            }
        } else if (args[0].equalsIgnoreCase("nuke")) {
            if (args.length >= 3) {
                World w = Bukkit.getServer().getWorld(args[1]);
                if (args[2].equalsIgnoreCase("all")) {
                    sender.sendMessage("Killing: " + String.valueOf(plugin.getCreatureHandler(w).getCreatureCount()));
                    plugin.getCreatureHandler(w).killAll();
                } else {
                    sender.sendMessage("Killing: " + String.valueOf(plugin.getCreatureHandler(w).getCreatureCount(CreatureType.valueOf(args[2].toUpperCase()))));
                    plugin.getCreatureHandler(w).killAll(CreatureType.valueOf(args[2]));
                }
                sender.sendMessage("Nuked!");
            } else {
                sender.sendMessage("Usage: crowd nuke [world] [CreatureType,all]");
            }
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length >= 5) {

                BaseInfo info = plugin.getCreatureHandler(Bukkit.getServer().getWorld(args[1])).getBaseInfo(CreatureType.valueOf(args[2].toUpperCase()));

                if (info != null) {
                    if (args[3].equalsIgnoreCase("NatureDay")) {
                        info.setCreatureNatureDay(Nature.valueOf(args[4].toUpperCase()));
                    } else if (args[3].equalsIgnoreCase("NatureNight")) {
                        info.setCreatureNatureNight(Nature.valueOf(args[4].toUpperCase()));
                    } else if (args[3].equalsIgnoreCase("CollisionDamage")) {
                        info.setCollisionDamage(Integer.parseInt(args[4]));
                    } else if (args[3].equalsIgnoreCase("MiscDamage")) {
                        info.setMiscDamage(Integer.parseInt(args[4]));
                    } else if (args[3].equalsIgnoreCase("Health")) {
                        info.setHealth(Integer.parseInt(args[4]));
                    } else if (args[3].equalsIgnoreCase("TargetDistance")) {
                        info.setTargetDistance(Integer.parseInt(args[4]));
                    } else if (args[3].equalsIgnoreCase("BurnDay")) {
                        info.setBurnDay(Boolean.parseBoolean(args[4]));
                    } else if (args[3].equalsIgnoreCase("SpawnChance")) {
                        info.setSpawnChance(Float.parseFloat(args[4]));
                    } else if (args[3].equalsIgnoreCase("Enabled")) {
                        info.setEnabled(Boolean.parseBoolean(args[4]));
                    } else {
                        sender.sendMessage("Invalid setting!");
                        return true;
                    }

                    plugin.getCreatureHandler(Bukkit.getServer().getWorld(args[1])).setInfo(info, CreatureType.valueOf(args[2].toUpperCase()));
                    sender.sendMessage("Set creature info!");

                } else {
                    sender.sendMessage("That creature type does not exist!");
                }
            } else {
                sender.sendMessage("usage crowd set [World] [CreatureType] [Setting] [Value]");
            }
        } else if (args[0].equals("reloadConfig")) {
            plugin.loadConfigFile();
        }

        return true;
    }

}
