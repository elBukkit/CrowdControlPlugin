package org.elbukkit.crowdcontrol;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.elbukkit.crowdcontrol.commands.KillCommand;
import org.elbukkit.crowdcontrol.listener.EntityListener;

public class CrowdControlPlugin extends JavaPlugin {

    private EntityListener entityListener = new EntityListener();
    private SpawnControl spawnHandler = new SpawnControl(this);

    public static void main(String[] args) {
        System.out.println("This is a bukkit plugin!");
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getFullName() + " is enabled!");

        getCommand("kill").setExecutor(new KillCommand(this));
        getServer().getPluginManager().registerEvents(entityListener, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, spawnHandler, 0, 1);
    }

    public void onDisable() {
        // TODO Auto-generated method stub

    }

}
