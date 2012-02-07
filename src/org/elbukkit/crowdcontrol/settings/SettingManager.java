package org.elbukkit.crowdcontrol.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Modifier;

import org.bukkit.World;
import org.elbukkit.crowdcontrol.CrowdControlPlugin;
import org.elbukkit.crowdcontrol.entity.CreatureType;
import org.elbukkit.crowdcontrol.entity.EntityData;

import com.google.gson.GsonBuilder;

public class SettingManager {

    private CrowdControlPlugin plugin;

    public SettingManager(CrowdControlPlugin plugin) {
        this.plugin = plugin;
    }

    public void saveSetting(EntityData data, CreatureType type, World w) {
        File dir = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + w.getName());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String location = plugin.getDataFolder().getAbsolutePath() + File.separator + w.getName() + File.separator + type.getName() + ".json";
        File entityFile = new File(location);
        try {
            entityFile.createNewFile();
            String json = getGsonBuilder().create().toJson(data);
            Writer writer = new BufferedWriter(new FileWriter(entityFile));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            // TODO Error handling
            System.out.println("Save Error!");
        }
    }

    public EntityData getSetting(CreatureType type, World w) {
        String location = plugin.getDataFolder().getAbsolutePath() + File.separator + w.getName() + File.separator + type.getName() + ".json";
        File entityFile = new File(location);
        try {
            Reader reader = new BufferedReader(new FileReader(entityFile));
            EntityData data = getGsonBuilder().create().fromJson(reader, EntityData.class);
            return data;
        } catch (FileNotFoundException e) {
            EntityData data;
            try {
                data = CreatureType.getClassFromCreatureType(type).newInstance();
                saveSetting(data, type, w);
                return data;
            } catch (InstantiationException | IllegalAccessException e1) {
                // TODO Auto-generated catch block
                System.out.println("Setting creation error!");
            }
            return null;
        }
    }

    public GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
    }

    public void saveMasterSettings(MasterSettings data) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        String location = plugin.getDataFolder().getAbsolutePath() + File.separator + "MasterSettings.json";
        File settingFile = new File(location);
        try {
            settingFile.createNewFile();
            String json = getGsonBuilder().create().toJson(data);
            Writer writer = new BufferedWriter(new FileWriter(settingFile));
            writer.write(json);
            writer.close();
        } catch (IOException e) {

        }
    }

    public MasterSettings getMasterSettings() {
        String location = plugin.getDataFolder().getAbsolutePath() + File.separator + "MasterSettings.json";
        File settingFile = new File(location);
        try {
            Reader reader = new BufferedReader(new FileReader(settingFile));
            MasterSettings data = getGsonBuilder().create().fromJson(reader, MasterSettings.class);
            return data;
        } catch (FileNotFoundException e) {
            MasterSettings data = new MasterSettings();
            saveMasterSettings(data);
            return data;
        }
    }

}
