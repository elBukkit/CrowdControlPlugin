package org.elbukkit.crowdcontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.elbukkit.crowdcontrol.CrowdControlPlugin;

public class KillCommand implements CommandExecutor {

    private CrowdControlPlugin plugin;

    public KillCommand(CrowdControlPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }

        List<LivingEntity> entities = new ArrayList<LivingEntity>();
        if (sender instanceof Player) {
            entities.addAll(((Player) sender).getWorld().getLivingEntities());
        } else {
            for (World w : plugin.getServer().getWorlds()) {
                entities.addAll(w.getLivingEntities());
            }
        }

        if (args[0].equalsIgnoreCase("all")) {
            for (LivingEntity e : entities) {
                if (e instanceof Player) {
                    continue;
                }
                e.remove();
            }
        }
        return true;
    }

}
