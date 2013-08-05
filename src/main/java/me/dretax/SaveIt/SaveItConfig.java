package me.dretax.SaveIt;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: DreTaX
 * Date: 2013.07.12.
 * Time: 19:41
 */
public class SaveItConfig {
    protected boolean CheckForUpdates, EnableMsg, DisableDefaultWorldSave, SaveOnLogin, SaveOnQuit, SaveOnBlockBreak, SaveOnBlockPlace, SelfInventorySave, SavePlayersFully, Debug, PowerSave, SaveAllWorlds, BroadCastErrorIg, SaveOnDisable, EnableBackup, EnableBackupMSG;
    protected int SaveOnBlockBreakcount, SaveOnBlockPlacecount, SaveOnLoginCount, SaveOnQuitCount;
    protected FileConfiguration config;
    protected File configFile;
    protected double intv;
    protected Double StartOnAGivenHour;
    protected List<String> ExWorlds = Arrays.asList(new String[] { "world"});
    Main p;

    protected SaveItConfig(Main i) {
        this.p = i;
    }

    protected void create() {
        if(!Bukkit.getPluginManager().getPlugin("SaveIt").getDataFolder().exists()) Bukkit.getPluginManager().getPlugin("SaveIt").getDataFolder().mkdir();
        configFile = new File(Bukkit.getPluginManager().getPlugin("SaveIt").getDataFolder(), "config.yml");
        if ((configFile.exists())) {
            load();
        }
        else {
            configFile = new File(Bukkit.getPluginManager().getPlugin("SaveIt").getDataFolder(), "config.yml");
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            config = new YamlConfiguration();
            config.addDefault("DelayInMinutes", 10);
            config.addDefault("Worlds", ExWorlds);
            config.addDefault("EnableSaveMSG", true);
            config.addDefault("SaveMSG", "&aStarting world save...");
            config.addDefault("SaveMSG2", "&aWorld save completed!");
            config.addDefault("SavePlayersEverywhere", false);
            config.addDefault("CheckForUpdates", true);
            config.addDefault("DisableDefaultWorldSave", true);
            config.addDefault("ExtraOptions.SaveOnLogin", false);
            config.addDefault("ExtraOptions.SaveOnLoginCount", 50);
            config.addDefault("ExtraOptions.SaveOnQuit", false);
            config.addDefault("ExtraOptions.SaveOnQuitCount", 50);
            config.addDefault("ExtraOptions.SaveOnBlockBreak", false);
            config.addDefault("ExtraOptions.SaveOnBlockPlace", false);
            config.addDefault("ExtraOptions.SaveOnBlockBreakcount", 500);
            config.addDefault("ExtraOptions.SaveOnBlockPlacecount", 500);
            config.addDefault("ExtraOptions.SaveOnDisable", true);
            config.addDefault("ExtraOptions.EnableSelfInventorySave", false);
            config.addDefault("ExtraOptions.EnableDebugMSGs", false);
            config.addDefault("EnablePowerSave", false);
            config.addDefault("SaveAllWorlds", false);
            config.addDefault("BroadCastWorldErrorIg", false);
            config.addDefault("BackUp.EnableBackup", false);
            config.addDefault("BackUp.EnableBackupMSG", true);
            config.addDefault("BackUp.BackupHoursInterval", 1.0);
            config.addDefault("BackUp.WarningMSG", "&aWarning! Backup has been executed!");
            config.addDefault("BackUp.WarningMSG2", "&aBackup Finished!");
            config.options().copyDefaults(true);
            try {
                config.save(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            load();
        }

    }

    protected void load() {
        config = new YamlConfiguration();
        configFile = new File(Bukkit.getPluginManager().getPlugin("SaveIt").getDataFolder(), "config.yml");
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
		/*
		 * Regural Variables
		 */
        EnableMsg = config.getBoolean("EnableSaveMSG");
        CheckForUpdates = config.getBoolean("CheckForUpdates");
        SavePlayersFully = config.getBoolean("SavePlayersEverywhere");
        DisableDefaultWorldSave = config.getBoolean("DisableDefaultWorldSave");
        PowerSave = config.getBoolean("EnablePowerSave");
        SaveAllWorlds = config.getBoolean("SaveAllWorlds");
        BroadCastErrorIg = config.getBoolean("BroadCastWorldErrorIg");
        ExWorlds = config.getStringList("Worlds");
		/*
		 * Special Savings
		 */
        SaveOnLogin = config.getBoolean("ExtraOptions.SaveOnLogin");
        SaveOnLoginCount = config.getInt("ExtraOptions.SaveOnLoginCount");
        SaveOnQuit = config.getBoolean("ExtraOptions.SaveOnQuit");
        SaveOnQuitCount = config.getInt("ExtraOptions.SaveOnQuitCount");
        SaveOnBlockBreak = config.getBoolean("ExtraOptions.SaveOnBlockBreak");
        SaveOnBlockPlace = config.getBoolean("ExtraOptions.SaveOnBlockPlace");
        SaveOnBlockBreakcount = config.getInt("ExtraOptions.SaveOnBlockBreakcount");
        SaveOnBlockPlacecount = config.getInt("ExtraOptions.SaveOnBlockPlacecount");
        SaveOnDisable = config.getBoolean("ExtraOptions.SaveOnDisable");
        SelfInventorySave = config.getBoolean("ExtraOptions.EnableSelfInventorySave");
        Debug = config.getBoolean("ExtraOptions.EnableDebugMSGs");
        EnableBackup = config.getBoolean("BackUp.EnableBackup");
        EnableBackupMSG = config.getBoolean("BackUp.EnableBackupMSG");
        intv = config.getDouble("BackUp.BackupHoursInterval");
        config.getString("BackUp.WarningMSG");
        config.getString("BackUp.WarningMSG2");
        String startTime = config.getString("BackUp.time");
        if (startTime != null) {
            try {
                Date parsedTime = new SimpleDateFormat("HH:mm").parse(startTime);
                StartOnAGivenHour = p.h(parsedTime);
            } catch (ParseException ignored) {}
        }
    }
}
