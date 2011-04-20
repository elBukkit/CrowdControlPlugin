package com.elmakers.mine.bukkit.plugins.crowd.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import com.elmakers.mine.bukkit.plugins.crowd.dao.CommandSenderData;
import com.elmakers.mine.bukkit.plugins.crowd.dao.MaterialList;
import com.elmakers.mine.bukkit.plugins.crowd.dao.Message;
import com.elmakers.mine.bukkit.plugins.crowd.dao.PluginCommand;
import com.elmakers.mine.bukkit.plugins.crowd.dao.PluginData;
import com.elmakers.mine.bukkit.plugins.crowd.dao.WorldData;

/**
 * An interface for displaying data-driven messages and processing data-driven
 * commands.
 * 
 * @author NathanWolf
 * 
 */
public class PluginUtilities
{
    private static final Logger     log = Bukkit.getServer().getLogger();

    private final Plugin            owner;

    private PluginData              plugin;

    /**
     * Messaging constructor. Use to create an instance of Messaging for your
     * plugin.
     * 
     * This can also be done via persistence.getMessaging(plugin)
     * 
     * @param requestingPlugin
     *            The plugin requesting the messaging interface
     * @param persistence
     *            The Persistence reference to use for retrieving data
     * @param permissions
     *            The permissions manager
     */
    public PluginUtilities(Plugin requestingPlugin)
    {
        this.owner = requestingPlugin;

        // Retrieve or create the plugin data record for this plugin.
        PluginDescriptionFile pdfFile = requestingPlugin.getDescription();
        String pluginId = pdfFile.getName();
        plugin = requestingPlugin.getDatabase().find(PluginData.class).where().idEq(pluginId).findUnique();
        if (plugin == null)
        {
            plugin = new PluginData(requestingPlugin);
            requestingPlugin.getDatabase().save(plugin);
        }

        // Let the plugin bind its transient command and message instances
        if (plugin.getCommands().isEmpty() && plugin.getMessages().isEmpty())
        {
            List<Message> allMessages = new ArrayList<Message>();
            List<PluginCommand> allCommands = new ArrayList<PluginCommand>();
            allMessages = requestingPlugin.getDatabase().find(Message.class).findList();
            allCommands = requestingPlugin.getDatabase().find(PluginCommand.class).findList();
            plugin.initializeCache(allMessages, allCommands);
        }
    }

    protected boolean dispatch(List<Object> listeners, CommandSender sender, PluginCommand command, String commandString, String[] parameters)
    {
        if (command != null && command.checkCommand(sender, commandString))
        {
            boolean handledByChild = false;
            if (parameters != null && parameters.length > 0)
            {
                String[] childParameters = new String[parameters.length - 1];
                for (int i = 0; i < childParameters.length; i++)
                {
                    childParameters[i] = parameters[i + 1];
                }
                String childCommand = parameters[0];

                List<PluginCommand> subCommands = command.getChildren();
                if (subCommands != null)
                {
                    List<PluginCommand> commandsCopy = new ArrayList<PluginCommand>();
                    commandsCopy.addAll(subCommands);

                    for (PluginCommand child : commandsCopy)
                    {
                        handledByChild = dispatch(listeners, sender, child, childCommand, childParameters);
                        if (handledByChild)
                        {
                            return true;
                        }
                    }
                }
            }

            String callbackName = command.getCallbackMethod();
            if (callbackName == null || callbackName.length() <= 0)
            {
                // auto help for commands that only have sub-commands
                command.sendUse(sender);
                return true;
            }

            for (Object listener : listeners)
            {
                try
                {       	
                	// TOOD: Fix command senders, WAY too much of a pain in the ass right now. it keeps braeking :(
                    // List<CommandSenderData> senders = command.getSenders();
                	
                	// For now, hard-coded for Player:
                	if (sender instanceof Player)
                	{
                        try
                        {
                            Method customHandler;
                            customHandler = listener.getClass().getMethod(callbackName, Player.class, String[].class);
                            try
                            {
                                return (Boolean) customHandler.invoke(listener, (Player)sender, parameters);
                            }
                            catch (InvocationTargetException clientEx)
                            {
                                log.severe("Error invoking callback '" + callbackName);
                                clientEx.getTargetException().printStackTrace();
                                return false;
                            }
                            catch (Throwable clientEx)
                            {
                                log.severe("Error invoking trying to invoke callback '" + callbackName);
                                clientEx.printStackTrace();
                                return false;
                            }
                        }
                        catch (NoSuchMethodException e)
                        {
                        }
                    }

                    try
                    {
                        Method genericHandler;
                        genericHandler = listener.getClass().getMethod(callbackName, CommandSender.class, String[].class);
                        return (Boolean) genericHandler.invoke(listener, sender, parameters);
                    }
                    catch (NoSuchMethodException ex)
                    {
                    }
                }
                catch (SecurityException ex)
                {
                    log.warning("Persistence: Can't access callback method " + callbackName + " of " + listener.getClass().getName() + ", make sure it's public");
                }
                catch (IllegalArgumentException ex)
                {
                    log.warning("Persistence: Can't find callback method " + callbackName + " of " + listener.getClass().getName() + " with the correct signature, please consult the docs.");
                }
                catch (IllegalAccessException ex)
                {
                    log.warning("Persistence: Can't access callback method " + callbackName + " of " + listener.getClass().getName());
                }
                catch (InvocationTargetException ex)
                {
                    log.severe("Persistence: Error invoking callback method " + callbackName + " of " + listener.getClass().getName());
                    ex.printStackTrace();
                }
            }

            log.info("Persistence: Can't find callback '" + callbackName + "' for plugin " + plugin.getId());
        }

        return false;
    }

    /**
     * Dispatch any automatically bound command handlers.
     * 
     * Any commands registered with this plugin that around bound() to a command
     * handler will be automatically called.
     * 
     * For Player commands, the signature should be:
     * 
     * public boolean onMyCommand(Player player, String[] parameters) { }
     * 
     * For General commands, a CommandSender should be used in place of Player.
     * 
     * @param listeners
     *            The class that will handle the command callback
     * @param sender
     *            The sender of this command
     * @param baseCommand
     *            The base command issues
     * @param baseParameters
     *            Any parameters (or sub-commands) passed to the base command
     * @see PluginCommand#bind(String)
     */
    public boolean dispatch(List<Object> listeners, CommandSender sender, String baseCommand, String[] baseParameters)
    {
        List<PluginCommand> baseCommands = plugin.getCommands();
        if (baseCommands == null)
        {
            return false;
        }

        List<PluginCommand> commandsCopy = new ArrayList<PluginCommand>();
        commandsCopy.addAll(baseCommands);

        for (PluginCommand command : commandsCopy)
        {
            boolean success = dispatch(listeners, sender, command, baseCommand, baseParameters);
            if (success)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Dispatch any automatically bound command handlers.
     * 
     * Any commands registered with this plugin that around bound() to a command
     * handler will be automatically called.
     * 
     * For Player commands, the signature should be:
     * 
     * public boolean onMyCommand(Player player, String[] parameters) { }
     * 
     * For General commands, a CommandSender should be used in place of Player.
     * 
     * @param listener
     *            The class that will handle the command callback
     * @param sender
     *            The sender of this command
     * @param baseCommand
     *            The base command issues
     * @param baseParameters
     *            Any parameters (or sub-commands) passed to the base command
     * @see PluginCommand#bind(String)
     */
    public boolean dispatch(Object listener, CommandSender sender, String baseCommand, String[] baseParameters)
    {
        List<Object> listeners = new ArrayList<Object>();
        listeners.add(listener);
        return dispatch(listeners, sender, baseCommand, baseParameters);
    }

    /**
     * Retrieve a command description based on id, for a given sender
     * 
     * A command description can be used to easily process commands, including
     * commands with sub-commands.
     * 
     * @param commandName
     *            The command id to retrieve or create
     * @param defaultTooltip
     *            The default tooltip to use if this is a new command
     * @param defaultUsage
     *            The default usage string, more can be added
     * @param sender
     *            The sender that will issue this command
     * @return A command descriptor
     */
    public PluginCommand getCommand(String commandName, String defaultTooltip, String defaultUsage, CommandSenderData sender)
    {
        return plugin.getCommand(commandName, defaultTooltip, defaultUsage, sender);
    }

    /**
     * Retrieve a general command description based on id.
     * 
     * A command description can be used to easily process commands, including
     * commands with sub-commands.
     * 
     * This method automatically creates a general command that will be passed a
     * CommandSender for use as a server or in-game command.
     * 
     * @param commandName
     *            The command id to retrieve or create
     * @param defaultTooltip
     *            The default tooltip to use if this is a new command
     * @param defaultUsage
     *            The default usage string, more can be added
     * @return A command descriptor
     */
    public PluginCommand getGeneralCommand(String commandName, String defaultTooltip, String defaultUsage)
    {
        return getGeneralCommand(commandName, defaultTooltip, defaultUsage);
    }

    public MaterialList getMaterialList(String listName)
    {
        return plugin.getMaterialList(listName);
    }

    /**
     * Get a message based on id, or create one using a default.
     * 
     * @param id
     *            The message id
     * @param defaultString
     *            The default string to use if no value exists
     * @return The stored message, or defaultString if none exists
     */
    public Message getMessage(String id, String defaultString)
    {
        return plugin.getMessage(id, defaultString);
    }

    public Plugin getOwningPlugin()
    {
        return owner;
    }

    /**
     * Retrieve a player command description based on id.
     * 
     * A command description can be used to easily process commands, including
     * commands with sub-commands.
     * 
     * This method automatically creates a player-specific (in-game) command.
     * 
     * @param commandName
     *            The command id to retrieve or create
     * @param defaultTooltip
     *            The default tooltip to use if this is a new command
     * @param defaultUsage
     *            The default usage string, more can be added
     * @return A command descriptor
     */
    public PluginCommand getPlayerCommand(String commandName, String defaultTooltip, String defaultUsage)
    {
        return getPlayerCommand(commandName, defaultTooltip, defaultUsage);
    }

    public WorldData getWorld(Server server, String name)
    {
        WorldData data = owner.getDatabase().find(WorldData.class).where().idEq(name).findUnique();
        if (data == null)
        {
            List<World> worlds = server.getWorlds();
            for (World world : worlds)
            {
                if (world.getName().equalsIgnoreCase(name))
                {
                    data = new WorldData(name, world.getEnvironment());
                    owner.getDatabase().save(data);
                }
                break;
            }
        }

        return data;
    }

    public WorldData getWorld(Server server, World world)
    {
        WorldData data = owner.getDatabase().find(WorldData.class).where().idEq(world.getName()).findUnique();
        if (data == null)
        {
            data = new WorldData(world);
            owner.getDatabase().save(data);
        }
        else
        {
            data.update(world);
        }

        return data;
    }

    public WorldData loadWorld(Server server, String name, Environment defaultType)
    {
        WorldData data = getWorld(server, name);
        if (data == null)
        {
            data = new WorldData(name, defaultType);
            owner.getDatabase().save(data);
        }

        return data;
    }
}
