package me.dretax.SaveIt;

import org.bukkit.Bukkit;

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
	protected boolean CheckForUpdates, EnableMsg, DisableDefaultWorldSave, SaveOnLogin, SaveOnQuit, SaveOnBlockBreak, SaveOnBlockPlace, SelfInventorySave, SavePlayersFully, Debug, PowerSave, SaveAllWorlds, BroadCastErrorIg, SaveOnDisable, EnableBackup, EnableBackupMSG, AutoBackup, KickBackup, PurgeBackups, MaxBackups, Ch;
	protected int SaveOnBlockBreakcount, SaveOnBlockPlacecount, SaveOnLoginCount, SaveOnQuitCount, DateIntv, maxbackups, daysBack, chtime;
	protected long Date;
	protected String Decide;
	protected File configFile;
	protected double intv;
	protected Double StartOnAGivenHour;
	protected List<String> ExWorlds = Arrays.asList(new String[]{"world"}), Directory = Arrays.asList(new String[]{"thisisntgoingtobebackuped"});
	Main p;

	protected SaveItConfig(Main i) {
		this.p = i;
	}

	protected void create() {
		p.config = p.getConfig();
		p.config.addDefault("DelayInMinutes", 10);
		p.config.addDefault("Worlds", ExWorlds);
		p.config.addDefault("EnableSaveMSG", true);
		p.config.addDefault("SaveMSG", "&aStarting world save...");
		p.config.addDefault("SaveMSG2", "&aWorld save completed!");
		p.config.addDefault("SavePlayersEverywhere", false);
		p.config.addDefault("CheckForUpdates", true);
		p.config.addDefault("DisableDefaultWorldSave", false);
		p.config.addDefault("ExtraOptions.SaveOnLogin", false);
		p.config.addDefault("ExtraOptions.SaveOnLoginCount", 50);
		p.config.addDefault("ExtraOptions.SaveOnQuit", false);
		p.config.addDefault("ExtraOptions.SaveOnQuitCount", 50);
		p.config.addDefault("ExtraOptions.SaveOnBlockBreak", false);
		p.config.addDefault("ExtraOptions.SaveOnBlockPlace", false);
		p.config.addDefault("ExtraOptions.SaveOnBlockBreakcount", 500);
		p.config.addDefault("ExtraOptions.SaveOnBlockPlacecount", 500);
		p.config.addDefault("ExtraOptions.SaveOnDisable", true);
		p.config.addDefault("ExtraOptions.EnableSelfInventorySave", false);
		p.config.addDefault("ExtraOptions.EnableDebugMSGs", false);
		p.config.addDefault("EnablePowerSave", false);
		p.config.addDefault("SaveAllWorlds", false);
		p.config.addDefault("BroadCastWorldErrorIg", false);
		p.config.addDefault("EnableChunkUnloading", false);
		p.config.addDefault("ChTime", 30);
		p.config.addDefault("BackUp.EnableBackup", false);
		p.config.addDefault("BackUp.EnableBackupMSG", true);
		p.config.addDefault("BackUp.EnableAutoBackup", false);
		p.config.addDefault("BackUp.EnablePlayerKickWhileBackup", false);
		p.config.addDefault("BackUp.IntervalOrDay", "INTERVAL");
		p.config.addDefault("BackUp.BackupHoursInterval", 4.0);
		p.config.addDefault("BackUp.Date", 0);
		p.config.addDefault("BackUp.DateDayDelay", 7);
		p.config.addDefault("BackUp.EnableBackupPurge", false);
		p.config.addDefault("BackUp.AutoPurge", false);
		p.config.addDefault("BackUp.RemoveBackupXAfterDay", 4);
		p.config.addDefault("BackUp.EnableMaxBackups", true);
		p.config.addDefault("BackUp.MaxBackups", 10);
		p.config.addDefault("BackUp.WarningMSG", "&aWarning! Backup has been executed!");
		p.config.addDefault("BackUp.WarningMSG2", "&aBackup Finished!");
		p.config.addDefault("BackUp.DirectoryNotToBackup", Directory);
		p.config.options().copyDefaults(true);
		p.saveConfig();
		load();
	}

	protected void load() {
		p.config = p.getConfig();
		configFile = new File(Bukkit.getPluginManager().getPlugin("SaveIt").getDataFolder(), "config.yml");
		try {
			p.config.load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * Regural Variables
         */
		EnableMsg = p.config.getBoolean("EnableSaveMSG");
		CheckForUpdates = p.config.getBoolean("CheckForUpdates");
		SavePlayersFully = p.config.getBoolean("SavePlayersEverywhere");
		DisableDefaultWorldSave = p.config.getBoolean("DisableDefaultWorldSave");
		PowerSave = p.config.getBoolean("EnablePowerSave");
		SaveAllWorlds = p.config.getBoolean("SaveAllWorlds");
		BroadCastErrorIg = p.config.getBoolean("BroadCastWorldErrorIg");
		ExWorlds = p.config.getStringList("Worlds");
		Directory = p.config.getStringList("BackUp.DirectoryNotToBackup");
        /*
        * Special Savings
         */
		SaveOnLogin = p.config.getBoolean("ExtraOptions.SaveOnLogin");
		SaveOnLoginCount = p.config.getInt("ExtraOptions.SaveOnLoginCount");
		SaveOnQuit = p.config.getBoolean("ExtraOptions.SaveOnQuit");
		SaveOnQuitCount = p.config.getInt("ExtraOptions.SaveOnQuitCount");
		SaveOnBlockBreak = p.config.getBoolean("ExtraOptions.SaveOnBlockBreak");
		SaveOnBlockPlace = p.config.getBoolean("ExtraOptions.SaveOnBlockPlace");
		SaveOnBlockBreakcount = p.config.getInt("ExtraOptions.SaveOnBlockBreakcount");
		SaveOnBlockPlacecount = p.config.getInt("ExtraOptions.SaveOnBlockPlacecount");
		SaveOnDisable = p.config.getBoolean("ExtraOptions.SaveOnDisable");
		SelfInventorySave = p.config.getBoolean("ExtraOptions.EnableSelfInventorySave");
		Debug = p.config.getBoolean("ExtraOptions.EnableDebugMSGs");
		EnableBackup = p.config.getBoolean("BackUp.EnableBackup");
		EnableBackupMSG = p.config.getBoolean("BackUp.EnableBackupMSG");
		intv = p.config.getDouble("BackUp.BackupHoursInterval");
		AutoBackup = p.config.getBoolean("BackUp.EnableAutoBackup");
		KickBackup = p.config.getBoolean("BackUp.EnablePlayerKickWhileBackup");
		Date = p.config.getInt("BackUp.Date");
		DateIntv = p.config.getInt("BackUp.DateDayDelay");
		Decide = p.config.getString("BackUp.IntervalOrDay");
		PurgeBackups = p.config.getBoolean("BackUp.EnableBackupPurge");
		daysBack = p.config.getInt("BackUp.RemoveBackupXAfterDay");
		maxbackups = p.config.getInt("BackUp.MaxBackups");
		MaxBackups = p.config.getBoolean("BackUp.EnableMaxBackups");
		Ch = p.config.getBoolean("EnableChunkUnloading");
		chtime = p.config.getInt("ChTime");
		p.config.getString("BackUp.WarningMSG");
		p.config.getString("BackUp.WarningMSG2");
		p.reloadConfig();
		String startTime = p.config.getString("BackUp.time");
		if (startTime != null) {
			try {
				Date parsedTime = new SimpleDateFormat("HH:mm").parse(startTime);
				StartOnAGivenHour = p.h(parsedTime);
			} catch (ParseException ignored) {
			}
		}
	}
}
