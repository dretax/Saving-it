package me.dretax.SaveIt;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: DreTaX
 * Date: 2013.07.12.
 * Time: 19:41
 */
public class SaveItConfig {
    protected static boolean CheckForUpdates, EnableMsg, DisableDefaultWorldSave, SaveOnLogin, SaveOnQuit, SaveOnBlockBreak, SaveOnBlockPlace, SelfInventorySave, SavePlayersFully, Debug, PowerSave, SaveAllWorlds, BroadCastErrorIg;
    protected static int SaveOnBlockBreakcount, SaveOnBlockPlacecount, SaveOnLoginCount, SaveOnQuitCount;
    private static FileConfiguration config;
    private static File configFile;
    protected static List<String> ExWorlds = Arrays.asList(new String[] { "world"});

    public static void create() {
        config = new YamlConfiguration();
        configFile = new File(Bukkit.getPluginManager().getPlugin("SaveIt").getDataFolder(), "config.yml");

        if ((configFile.exists())) {
            load();
        } else {
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                config = new YamlConfiguration();
                config.set("DelayInMinutes", 10);
                config.set("Worlds", ExWorlds);
                config.set("EnableSaveMSG", true);
                config.set("SaveMSG2", "&aStarting world save...");
                config.set("SaveMSG2", "&aWorld save completed!");
                config.set("SavePlayersEverywhere", false);
                config.set("CheckForUpdates", true);
                config.set("DisableDefaultWorldSave", true);
                config.set("ExtraOptions.SaveOnLogin", false);
                config.set("ExtraOptions.SaveOnLoginCount", 50);
                config.set("ExtraOptions.SaveOnQuit", false);
                config.set("ExtraOptions.SaveOnQuitCount", 50);
                config.set("ExtraOptions.SaveOnBlockBreak", false);
                config.set("ExtraOptions.SaveOnBlockPlace", false);
                config.set("ExtraOptions.SaveOnBlockBreakcount", 500);
                config.set("ExtraOptions.SaveOnBlockPlacecount", 500);
                config.set("ExtraOptions.SaveOnDisable", true);
                config.set("ExtraOptions.EnableSelfInventorySave", false);
                config.set("ExtraOptions.EnableDebugMSGs", false);
                config.set("EnablePowerSave", false);
                config.set("SaveAllWorlds", false);
                config.set("BroadCastWorldErrorIg", false);
                try {
                    config.save(configFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                load();

            }
        }

    }

    public static void load() {
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
		 * Configuration and Command Definitions
		 */
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
        SelfInventorySave = config.getBoolean("ExtraOptions.EnableSelfInventorySave");
        Debug = config.getBoolean("ExtraOptions.EnableDebugMSGs");


    }

}
