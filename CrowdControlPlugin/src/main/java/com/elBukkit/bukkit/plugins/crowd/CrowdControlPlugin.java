package com.elBukkit.bukkit.plugins.crowd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.elBukkit.bukkit.plugins.crowd.creature.CreatureHandler;
import com.elBukkit.bukkit.plugins.crowd.creature.DamageHandler;
import com.elBukkit.bukkit.plugins.crowd.rules.MaxRule;
import com.elBukkit.bukkit.plugins.crowd.rules.Rule;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnEnvironmentRule;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnHeightRule;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnLightRule;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnMaterialRule;
import com.elBukkit.bukkit.plugins.crowd.rules.SpawnReplaceRule;
import com.elBukkit.bukkit.plugins.crowd.rules.TargetPlayerRule;

/*
 * CrowdControl plugin
 * 
 * @author Andrew Querol(WinSock)
 */

public class CrowdControlPlugin extends JavaPlugin {

    private CrowdEntityListener entityListener = new CrowdEntityListener(this);
    public Set<Info> pendingSpawn = new HashSet<Info>();
    private PluginDescriptionFile pdf;
    public Map<Class<? extends Rule>, String> ruleCommands;

    private int maxPerWorld = 200;
    private int maxPerChunk = 4;
    private File configFile;

    public RuleHandler ruleHandler;
    public Map<World, CreatureHandler> creatureHandlers = new HashMap<World, CreatureHandler>();

    public sqlCore dbManage; // import SQLite lib

    public void onDisable() {
        System.out.println(pdf.getFullName() + " is disabled!");
    }

    public void onEnable() {
        pdf = this.getDescription();
        System.out.println(pdf.getFullName() + " is enabled!");

        ruleCommands = new HashMap<Class<? extends Rule>, String>();
        ruleCommands.put(MaxRule.class, "[max number]");
        ruleCommands.put(SpawnEnvironmentRule.class, "[NORMAL,NETHER]");
        ruleCommands.put(SpawnHeightRule.class, "[max,min]");
        ruleCommands.put(SpawnLightRule.class, "[max,min]");
        ruleCommands.put(SpawnMaterialRule.class, "[material name]");
        ruleCommands.put(TargetPlayerRule.class, "[player,targetable(true,false)]");
        ruleCommands.put(SpawnReplaceRule.class, "[creature name]");

        if (!this.getDataFolder().exists())
            this.getDataFolder().mkdirs(); // Create dir if it doesn't exist

        String prefix = "[CrowdControl]";
        String dbName = pdf.getName() + ".db";

        dbManage = new sqlCore(this.getServer().getLogger(), prefix, dbName, this.getDataFolder().getAbsolutePath());
        try {
            ruleHandler = new RuleHandler(dbManage, this);
        } catch (Exception e) {
            e.printStackTrace();
            this.setEnabled(false);
            return;
        }

        configFile = new File(this.getDataFolder().getAbsolutePath() + File.separator + "config.txt");

        try {
            if (!configFile.exists()) {
                configFile.createNewFile();

                PrintWriter globalConfigWriter = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));

                globalConfigWriter.println("maxPerWorld:" + String.valueOf(this.maxPerWorld));
                globalConfigWriter.println("maxPerChunk:" + String.valueOf(this.maxPerChunk));

                globalConfigWriter.close();
            }

            Scanner globalConfigReader = new Scanner(new FileInputStream(configFile));
            globalConfigReader.useDelimiter(System.getProperty("line.separator"));

            while (globalConfigReader.hasNext()) {
                String[] data = processLine(globalConfigReader.next());

                if (data != null) {
                    if (data[0].equalsIgnoreCase("maxPerWorld")) {
                        this.maxPerWorld = Integer.parseInt(data[1]);
                    } else if (data[0].equalsIgnoreCase("maxPerChunk")) {
                        this.maxPerChunk = Integer.parseInt(data[1]);
                    }
                }
            }

            globalConfigReader.reset();

        } catch (IOException e) {
            System.out.println("Failed to read config file!");
            this.setEnabled(false);
        }

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest, this);
        pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Highest, this);
        pm.registerEvent(Type.ENTITY_COMBUST, entityListener, Priority.Highest, this);
        pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
        pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);

        // Register command
        getCommand("crowd").setExecutor(new CrowdCommand(this));

        // Register the damage handler
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new DamageHandler(this), 0, 20);

        for (World w : Bukkit.getServer().getWorlds()) {

            CreatureHandler cHandler = getCreatureHandler(w); // Create all of
                                                              // the creature
                                                              // handlers

            for (LivingEntity e : w.getLivingEntities()) {
                cHandler.addLivingEntity(e); // Add existing
            }
        }
    }

    private String[] processLine(String aLine) {
        Scanner scanner = new Scanner(aLine);
        scanner.useDelimiter(":");
        if (scanner.hasNext()) {
            String[] returnData = { "", "" };
            returnData[0] = scanner.next();
            returnData[1] = scanner.next();
            return returnData;
        } else {
            System.out.println("Empty or invalid line. Unable to process.");
            return null;
        }
    }

    public void setMaxPerChunk(int max) throws IOException {
        PrintWriter globalConfigWriter = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));

        this.maxPerChunk = max;

        this.configFile.delete();
        this.configFile.createNewFile();

        globalConfigWriter.println("maxPerWorld:" + String.valueOf(this.maxPerWorld));
        globalConfigWriter.println("maxPerChunk:" + String.valueOf(this.maxPerChunk));

        globalConfigWriter.close();
    }

    public void setMaxPerWorld(int max) throws IOException {

        PrintWriter globalConfigWriter = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));

        this.maxPerWorld = max;

        this.configFile.delete();
        this.configFile.createNewFile();

        globalConfigWriter.println("maxPerWorld:" + String.valueOf(this.maxPerWorld));
        globalConfigWriter.println("maxPerChunk:" + String.valueOf(this.maxPerChunk));

        globalConfigWriter.close();
    }

    public int getMaxPerWorld() {
        return this.maxPerWorld;
    }

    public int getMaxPerChunk() {
        return this.maxPerChunk;
    }

    public CreatureHandler getCreatureHandler(World w) {
        if (creatureHandlers.containsKey(w)) {
            return creatureHandlers.get(w);
        } else {
            CreatureHandler creatureHandler;
            try {
                creatureHandler = new CreatureHandler(dbManage, w, this);
                // Register the despawner
                getServer().getScheduler().scheduleSyncRepeatingTask(this, creatureHandler, 0, 20);
                creatureHandlers.put(w, creatureHandler);
                return creatureHandler;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
