package me.dretax.SaveIt;

import me.dretax.SaveIt.tasks.SaveItTaskManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

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
	public boolean CheckForUpdates, EnableMsg, DisableDefaultWorldSave, SaveOnLogin, SaveOnQuit, SaveOnBlockBreak, SaveOnBlockPlace, SelfInventorySave, SavePlayersFully, Debug, PowerSave, SaveAllWorlds, BroadCastErrorIg, EnableBackup, EnableBackupMSG, AutoBackup, KickBackup, PurgeBackups, MaxBackups;
	public int SaveOnBlockBreakcount, SaveOnBlockPlacecount, SaveOnLoginCount, SaveOnQuitCount, DateIntv, maxbackups, daysBack;
	public long Date;
	public String Decide, BackUpKickMSG;
	public File configFile;
	public int intv;
	public Double StartOnAGivenHour;
	public List<String> ExWorlds = Arrays.asList(new String[]{"world"}), Directory = Arrays.asList(new String[]{"thisisntgoingtobebackuped"});
	private Main p = Main.getInstance();
	private FileConfiguration config;

	protected void create() {
		this.config = p.getConfig();
		getPluginConfig().addDefault("DelayInMinutes", 10);
		getPluginConfig().addDefault("Worlds", ExWorlds);
		getPluginConfig().addDefault("EnableSaveMSG", true);
		getPluginConfig().addDefault("SaveMSG", "&aStarting world save...");
		getPluginConfig().addDefault("SaveMSG2", "&aWorld save completed!");
		getPluginConfig().addDefault("SavePlayersEverywhere", false);
		getPluginConfig().addDefault("CheckForUpdates", true);
		getPluginConfig().addDefault("DisableDefaultWorldSave", false);
		getPluginConfig().addDefault("ExtraOptions.SaveOnLogin", false);
		getPluginConfig().addDefault("ExtraOptions.SaveOnLoginCount", 50);
		getPluginConfig().addDefault("ExtraOptions.SaveOnQuit", false);
		getPluginConfig().addDefault("ExtraOptions.SaveOnQuitCount", 50);
		getPluginConfig().addDefault("ExtraOptions.SaveOnBlockBreak", false);
		getPluginConfig().addDefault("ExtraOptions.SaveOnBlockPlace", false);
		getPluginConfig().addDefault("ExtraOptions.SaveOnBlockBreakcount", 500);
		getPluginConfig().addDefault("ExtraOptions.SaveOnBlockPlacecount", 500);
		getPluginConfig().addDefault("ExtraOptions.EnableSelfInventorySave", false);
		getPluginConfig().addDefault("ExtraOptions.EnableDebugMSGs", false);
		getPluginConfig().addDefault("EnablePowerSave", false);
		getPluginConfig().addDefault("SaveAllWorlds", false);
		getPluginConfig().addDefault("BroadCastWorldErrorIg", false);
		getPluginConfig().addDefault("BackUp.EnableBackup", false);
		getPluginConfig().addDefault("BackUp.EnableBackupMSG", true);
		getPluginConfig().addDefault("BackUp.EnableAutoBackup", false);
		getPluginConfig().addDefault("BackUp.EnablePlayerKickWhileBackup", false);
		getPluginConfig().addDefault("BackUp.KickMSG", "Server is making a backup...");
		getPluginConfig().addDefault("BackUp.IntervalOrDay", "INTERVAL");
		getPluginConfig().addDefault("BackUp.BackupHoursInterval", 4);
		getPluginConfig().addDefault("BackUp.Date", 0);
		getPluginConfig().addDefault("BackUp.DateDayDelay", 7);
		getPluginConfig().addDefault("BackUp.EnableBackupPurge", false);
		getPluginConfig().addDefault("BackUp.AutoPurge", false);
		getPluginConfig().addDefault("BackUp.RemoveBackupXAfterDay", 4);
		getPluginConfig().addDefault("BackUp.EnableMaxBackups", true);
		getPluginConfig().addDefault("BackUp.MaxBackups", 10);
		getPluginConfig().addDefault("BackUp.WarningMSG", "&aWarning! Backup has been executed!");
		getPluginConfig().addDefault("BackUp.WarningMSG2", "&aBackup Finished!");
		getPluginConfig().addDefault("BackUp.DirectoryNotToBackup", Directory);
		getPluginConfig().options().copyDefaults(true);
		p.saveConfig();
		load();
	}

	protected void load() {
		configFile = new File(Bukkit.getPluginManager().getPlugin("SaveIt").getDataFolder(), "config.yml");
		try {
			getPluginConfig().load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * Regural Variables
         */
		EnableMsg = getPluginConfig().getBoolean("EnableSaveMSG");
		CheckForUpdates = getPluginConfig().getBoolean("CheckForUpdates");
		SavePlayersFully = getPluginConfig().getBoolean("SavePlayersEverywhere");
		DisableDefaultWorldSave = getPluginConfig().getBoolean("DisableDefaultWorldSave");
		PowerSave = getPluginConfig().getBoolean("EnablePowerSave");
		SaveAllWorlds = getPluginConfig().getBoolean("SaveAllWorlds");
		BroadCastErrorIg = getPluginConfig().getBoolean("BroadCastWorldErrorIg");
		ExWorlds = getPluginConfig().getStringList("Worlds");
		Directory = getPluginConfig().getStringList("BackUp.DirectoryNotToBackup");
		/*
        * Special Savings
         */
		SaveOnLogin = getPluginConfig().getBoolean("ExtraOptions.SaveOnLogin");
		SaveOnLoginCount = getPluginConfig().getInt("ExtraOptions.SaveOnLoginCount");
		SaveOnQuit = getPluginConfig().getBoolean("ExtraOptions.SaveOnQuit");
		SaveOnQuitCount = getPluginConfig().getInt("ExtraOptions.SaveOnQuitCount");
		SaveOnBlockBreak = getPluginConfig().getBoolean("ExtraOptions.SaveOnBlockBreak");
		SaveOnBlockPlace = getPluginConfig().getBoolean("ExtraOptions.SaveOnBlockPlace");
		SaveOnBlockBreakcount = getPluginConfig().getInt("ExtraOptions.SaveOnBlockBreakcount");
		SaveOnBlockPlacecount = getPluginConfig().getInt("ExtraOptions.SaveOnBlockPlacecount");
		SelfInventorySave = getPluginConfig().getBoolean("ExtraOptions.EnableSelfInventorySave");
		Debug = getPluginConfig().getBoolean("ExtraOptions.EnableDebugMSGs");
		EnableBackup = getPluginConfig().getBoolean("BackUp.EnableBackup");
		EnableBackupMSG = getPluginConfig().getBoolean("BackUp.EnableBackupMSG");
		intv = getPluginConfig().getInt("BackUp.BackupHoursInterval");
		AutoBackup = getPluginConfig().getBoolean("BackUp.EnableAutoBackup");
		KickBackup = getPluginConfig().getBoolean("BackUp.EnablePlayerKickWhileBackup");
		Date = getPluginConfig().getInt("BackUp.Date");
		DateIntv = getPluginConfig().getInt("BackUp.DateDayDelay");
		Decide = getPluginConfig().getString("BackUp.IntervalOrDay");
		PurgeBackups = getPluginConfig().getBoolean("BackUp.EnableBackupPurge");
		daysBack = getPluginConfig().getInt("BackUp.RemoveBackupXAfterDay");
		maxbackups = getPluginConfig().getInt("BackUp.MaxBackups");
		MaxBackups = getPluginConfig().getBoolean("BackUp.EnableMaxBackups");
		BackUpKickMSG = getPluginConfig().getString("BackUp.KickMSG");
		p.reloadConfig();
		String startTime = getPluginConfig().getString("BackUp.time");
		if (startTime != null) {
			try {
				Date parsedTime = new SimpleDateFormat("HH:mm").parse(startTime);
				StartOnAGivenHour = SaveItTaskManager.h(parsedTime);
			} catch (ParseException ignored) {
			}
		}
	}

	public FileConfiguration getPluginConfig() {
		this.config = p.getConfig();
		return this.config;
	}
}
