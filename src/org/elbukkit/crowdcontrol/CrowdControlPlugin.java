package org.elbukkit.crowdcontrol;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.elbukkit.crowdcontrol.commands.KillCommand;
import org.elbukkit.crowdcontrol.listener.EntityListener;
import org.elbukkit.crowdcontrol.settings.SettingManager;

public class CrowdControlPlugin extends JavaPlugin {

    private EntityListener entityListener;
    private CreatureControl spawnHandler;
    private SettingManager settingManager;

    public static void main(String[] args) {
        System.out.println("This is a bukkit plugin!");
    }

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getFullName() + " is enabled!");

        settingManager = new SettingManager(this);
        entityListener = new EntityListener(this);
        spawnHandler = new CreatureControl(this);

        getCommand("kill").setExecutor(new KillCommand(this));
        getServer().getPluginManager().registerEvents(entityListener, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, spawnHandler, 0, 10);

    }

    @Override
    public void onDisable() {
        // TODO Auto-generated method stub

    }

    public SettingManager getSettingManager() {
        return settingManager;
    }

    public CreatureControl getCreatureController() {
        return spawnHandler;
    }

}
