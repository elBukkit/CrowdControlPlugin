package org.elbukkit.crowdcontrol;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.elbukkit.crowdcontrol.commands.KillCommand;
import org.elbukkit.crowdcontrol.entity.CreatureType;
import org.elbukkit.crowdcontrol.listener.EntityListener;
import org.elbukkit.crowdcontrol.settings.SettingManager;

public class CrowdControlPlugin extends JavaPlugin {

    private EntityListener entityListener = new EntityListener(this);
    private CreatureControl spawnHandler = new CreatureControl(this);

    public static void main(String[] args) {
        System.out.println("This is a bukkit plugin!");
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getFullName() + " is enabled!");

        getCommand("kill").setExecutor(new KillCommand(this));
        getServer().getPluginManager().registerEvents(entityListener, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, spawnHandler, 0, 1);

        if (!this.getDataFolder().exists()) {
            for (CreatureType c : CreatureType.values()) {
                for (World w : Bukkit.getServer().getWorlds()) {
                    new SettingManager(this).getSetting(c, w);
                }
            }
        }
    }

    public void onDisable() {
        // TODO Auto-generated method stub

    }

}
