package com.elmakers.mine.bukkit.plugins.crowd.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A data class for encapsulating and storing a Command object.
 * 
 * @author NathanWolf
 * 
 */

@Entity()
@Table(name = "cc_command")
public class PluginCommand implements
        Comparable<PluginCommand>
{
    private static final String                  indent   = "  ";
	@NotNull
    private String                               callbackMethod;
	@Embedded
    private List<PluginCommand>                  children;
    @NotEmpty
    private String                               command;
    @NotNull
    private boolean                              enabled  = true;
    @Id
    private int                                  id;
    @NotNull
    private PluginCommand                        parent;
    @NotNull
    private PluginData                           plugin;
    @Embedded
    private List<CommandSenderData>              senders;
    @NotEmpty
    private String                               tooltip;
    @Embedded
    private List<String>                         usage;

    // Transient data
    private final HashMap<String, PluginCommand> childMap = new HashMap<String, PluginCommand>();

    /**
     * The default constructor, used by Persistence to create instances.
     * 
     * Use PluginUtilities to create PluginCommands.
     * 
     * @see com.elmakers.mine.bukkit.utilities.PluginUtilities#getCommand(String,
     *      String, String, CommandSenderData, String, PermissionType)
     */
    public PluginCommand()
    {

    }

    protected PluginCommand(PluginData plugin, String commandName, String tooltip)
    {
        this.plugin = plugin;
        this.command = commandName;
        this.tooltip = tooltip;
    }

    /**
     * Use this to add an additional command sender that is able to receive this
     * type of message.
     * 
     * @param sender
     *            the command sender to add
     */
    public void addSender(CommandSenderData sender)
    {
        if (sender == null)
        {
            return;
        }

        if (senders == null)
        {
            senders = new ArrayList<CommandSenderData>();
        }
        if (!senders.contains(sender))
        {
            senders.add(sender);
        }
    }

    /**
     * Add a command to this command as a sub-command.
     * 
     * Sub-commands are activated using parameters. So:
     * 
     * /persist list global.player.NathanWolf
     * 
     * Consists of the main Command "persist", one sub-command "list", and one
     * parameter "global.player.NathanWolf".
     * 
     * @param command
     *            The command to add as a sub-command of this one
     */
    protected void addSubCommand(PluginCommand command)
    {
        if (children == null)
        {
            children = new ArrayList<PluginCommand>();
        }

        // Child will self-register!
        command.setParent(this);

        // Pass on any senders
        if (senders != null)
        {
            for (CommandSenderData sender : senders)
            {
                command.addSender(sender);
            }
        }
    }

    protected void addToParent()
    {
        if (parent != null)
        {
            if (parent.children == null)
            {
                parent.children = new ArrayList<PluginCommand>();
            }
            if (parent.childMap.get(command) == null)
            {
                parent.children.add(this);
                parent.childMap.put(command, this);
            }
        }
    }

    /**
     * Use this to add an additional usage (example) string to this command.
     * 
     * @param use
     *            The usage string
     */
    public void addUsage(String use)
    {
        if (use == null || use.length() <= 0)
        {
            return;
        }

        if (usage == null)
        {
            usage = new ArrayList<String>();
        }
        if (!usage.contains(use))
        {
            usage.add(use);
        }
    }

    /**
     * Set up automatic command binding for this command.
     * 
     * If you dispatch commands with messaging.dispatch, this command will
     * automatically call the given method on the listener class if executed.
     * 
     * For Player commands, the signature should be:
     * 
     * public boolean onMyCommand(Player player, String[] parameters) { }
     * 
     * For General commands, a CommandSender should be used in place of Player.
     * 
     * @param methodName
     * @see com.elmakers.mine.bukkit.utilities.PluginUtilities#dispatch(Object,
     *      CommandSender, String, String[])
     */
    public void bind(String methodName)
    {
        callbackMethod = methodName;
    }

    /**
     * Check to see if this command matches a given command string.
     * 
     * If the command sender is a player, a permissions check will be done.
     * 
     * @param sender
     *            the sender requesting access.
     * @param commandString
     *            The command string to check
     * @return Whether or not the command succeeded
     */
    public boolean checkCommand(CommandSender sender, String commandString)
    {
        return command.equals(commandString) || command.equals(commandString.toLowerCase());
    }

    public int compareTo(PluginCommand compare)
    {
        return command.compareTo(compare.getCommand());
    }

    public String getCallbackMethod()
    {
        return callbackMethod;
    }

    public List<PluginCommand> getChildren()
    {
        return children;
    }
    
    public String getCommand()
    {
        return command;
    }

    public String getDefaultPermissionNode()
    {
        String pNode = "";
        PluginCommand addParent = parent;
        while (addParent != null)
        {
            pNode = addParent.command + "." + pNode;
            addParent = addParent.parent;
        }
        pNode = plugin.getId() + ".commands." + pNode + command;
        return pNode;
    }

    public int getId()
    {
        return id;
    }

    public PluginCommand getParent()
    {
        return parent;
    }

    protected String getPath()
    {
        String path = command;
        if (parent != null)
        {
            path = parent.getPath() + " " + path;
        }
        return path;
    }

    public PluginData getPlugin()
    {
        return plugin;
    }

    public List<CommandSenderData> getSenders()
    {
        return senders;
    }

    /**
     * Get or create a sub-command of this command.
     * 
     * @param subCommandName
     *            The sub-command name
     * @param defaultTooltip
     *            The default tooltip
     * @param defaultUsage
     *            The default usage string
     * @return A new command object
     */
    public PluginCommand getSubCommand(String subCommandName, String defaultTooltip, String defaultUsage)
    {
        PluginCommand child = childMap.get(subCommandName);
        if (child == null)
        {
            child = new PluginCommand(plugin, subCommandName, defaultTooltip);
            child.addUsage(defaultUsage);

            // adds senders
            addSubCommand(child);
        }

        return child;
    }

    public String getTooltip()
    {
        return tooltip;
    }

    public List<String> getUsage()
    {
        return usage;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    protected void removeFromParent()
    {
        if (parent != null)
        {
            if (parent.childMap.get(command) != null)
            {
                parent.children.remove(this);
                parent.childMap.remove(command);
            }
        }
    }

    /**
     * Use this to display a help message for this command to the given sender.
     * 
     * CommandSender may be a player, server console, etc.
     * 
     * @param sender
     *            The CommandSender (e.g. Player) to display help to
     * @param prefix
     *            A prefix, such as "Use: " to put in front of the first line
     * @param showUsage
     *            Whether or not to show detailed usage information
     * @param showSubCommands
     *            Whether or not to also display a tree of sub-command usage
     */
    public void sendHelp(CommandSender sender, String prefix, boolean showUsage, boolean showSubCommands)
    {
        boolean useSlash = sender instanceof Player;
        String slash = useSlash ? "/" : "";
        String currentIndent = "";

        if (callbackMethod != null)
        {
            String message = currentIndent + slash + getPath() + " : " + tooltip;
            sender.sendMessage(prefix + message);
            currentIndent += indent;

            if (showUsage && usage != null)
            {
                for (String exampleUse : usage)
                {
                    sender.sendMessage(currentIndent + " ex: " + getPath() + " " + exampleUse);
                }
            }
        }

        if (showSubCommands && children != null)
        {
            for (PluginCommand child : children)
            {
                child.sendHelp(sender, "", showUsage, showSubCommands);
            }
        }
    }

    /**
     * Use to send a short informational help message
     * 
     * This can be used when the player has mis-entered parameters or some other
     * exceptional case.
     * 
     * @param sender
     *            The CommandSender to reply to
     */
    public void sendShortHelp(CommandSender sender)
    {
        sendHelp(sender, "Use: ", false, false);
    }

    public void sendUse(CommandSender sender)
    {
        sendHelp(sender, "Use: ", true, true);
    }

    public void setCommand(String command)
    {
        // Must do this here too, since we maintain a hash of sub-commands by
        // command name!
        removeFromParent();
        this.command = command;
        addToParent();
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setParent(PluginCommand parent)
    {
        removeFromParent();
        this.parent = parent;
        addToParent();
    }

    public void setPlugin(PluginData plugin)
    {
        this.plugin = plugin;
    }

    public void setSenders(List<CommandSenderData> senders)
    {
        this.senders = senders;
    }

    public void setTooltip(String tooltip)
    {
        this.tooltip = tooltip;
    }

    public void setUsage(List<String> usage)
    {
        this.usage = usage;
    }
}
