package me.dretax.SaveIt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import me.dretax.SaveIt.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class Main extends JavaPlugin
{
	/*
	 * 
	 * @Author: DreTaX
	 * 
	 */
	protected int Delay;
	protected boolean Worldd;
	protected static boolean EnableMsg;
	protected static boolean CheckForUpdates;
	protected static boolean DisableDefaultWorldSave;
	protected static boolean SaveOnLogin;
	protected static boolean SaveOnQuit;
	protected static boolean SaveOnBlockBreak;
	protected static boolean SaveOnBlockPlace;
	protected static boolean SelfInventorySave;
	protected String MSG;
	protected String MSG2;
	protected PluginManager _pm;
	protected static ConsoleCommandSender _cs;
	protected static final String _prefix = ChatColor.AQUA + "[SaveIt] ";
	protected static List<String> ExWorlds = Arrays.asList(new String[] { "world", "world_nether", "world_the_end"});
	protected Boolean isLatest;
	protected String latestVersion;
	protected Main plugin;
	protected final SaveItExpansions expansions = new SaveItExpansions(plugin);
	protected static FileConfiguration config;
	protected static int SaveOnBlockBreakcount;
	protected static int SaveOnBlockPlacecount;
	Logger log = Logger.getLogger("Minecraft");
	
	public void onDisable()
	{
		super.onDisable();
		WorldSave();
	}
	
  
	public void onEnable() {
		this._pm = getServer().getPluginManager();
		_cs = getServer().getConsoleSender();
		/*
		 * Metrics
		 */
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
			sendConsoleMessage(ChatColor.GREEN + "SaveIt Metrics Successfully Enabled!");
		}
		// Couldn't Connect.
		catch (IOException localIOException) {
		}
		/*
		 * Configuration and Command Definitions
		 */
		getCommand("saveit").setExecutor(this);
		config = this.getConfig();
		config.addDefault("DelayInMinutes", Integer.valueOf(10));
		config.addDefault("Worlds", ExWorlds);
		config.addDefault("EnableSaveMSG", true);
		config.addDefault("SaveMSG", "&aStarting world save...");
		config.addDefault("SaveMSG2", "&aWorld save completed!");
		config.addDefault("CheckForUpdates", true);
		config.addDefault("DisableDefaultWorldSave", false);
		config.addDefault("ExtraOptions.SaveOnLogin", false);
		config.addDefault("ExtraOptions.SaveOnQuit", false);
		config.addDefault("ExtraOptions.SaveOnBlockBreak", false);
		config.addDefault("ExtraOptions.SaveOnBlockPlace", false);
		config.addDefault("ExtraOptions.SaveOnBlockBreakcount", Integer.valueOf(500));
		config.addDefault("ExtraOptions.SaveOnBlockPlacecount", Integer.valueOf(500));
		config.addDefault("ExtraOptions.EnableSelfInventorySave", true);
		config.options().copyDefaults(true);
		saveConfig();
		/*
		 * Regural Variables
		 */
		EnableMsg = config.getBoolean("EnableSaveMSG");
		CheckForUpdates = config.getBoolean("CheckForUpdates");
		DisableDefaultWorldSave = config.getBoolean("DisableDefaultWorldSave");
		/*
		 * Special Savings
		 */
		SaveOnLogin = config.getBoolean("ExtraOptions.SaveOnLogin");
		SaveOnQuit = config.getBoolean("ExtraOptions.SaveOnQuit");
		SaveOnBlockBreak = config.getBoolean("ExtraOptions.SaveOnBlockBreak");
		SaveOnBlockPlace = config.getBoolean("ExtraOptions.SaveOnBlockPlace");
		SaveOnBlockBreakcount = config.getInt("ExtraOptions.SaveOnBlockBreakcount");
		SaveOnBlockPlacecount = config.getInt("ExtraOptions.SaveOnBlockPlacecount");
		SelfInventorySave = config.getBoolean("ExtraOptions.EnableSelfInventorySave");
		/*
		 * Delays
		 */
		
		int delay = config.getInt("DelayInMinutes");
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			public void run() {
				WorldSave();
			}
		}
		, 1200L * delay, 1200L * delay);
		
		/*
		 * Others
		 */
		
		if (DisableDefaultWorldSave) {
			for (World world : Bukkit.getWorlds()) {
				world.setAutoSave(false);
			}
		}
		
		SaveItUpdate updateChecker = new SaveItUpdate(this);
		if (CheckForUpdates) {
			this.isLatest = updateChecker.isLatest();
			this.latestVersion = updateChecker.getUpdateVersion();
		}
		
		_pm.registerEvents(this.expansions, this);
		sendConsoleMessage(ChatColor.GREEN + "SaveIt Successfully Enabled!");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("save")) {
				if (sender.hasPermission("saveit.save")) {
					WorldSave();
				}
				else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("saveit.reload")) {
					ConfigReload();
					sender.sendMessage(_prefix + ChatColor.GREEN + "Config Reloaded! Check Console for Errors if Config not Working");
				}
				else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			if (args[0].equalsIgnoreCase("selfsave")) {
				if (SelfInventorySave) {
					if (sender.hasPermission("saveit.selfsave")) {
						if (!(sender instanceof Player)) {
							sender.sendMessage(_prefix + ChatColor.GREEN + "This command can only be run by a player.");
						}
						else {
							((Player) sender).saveData();
							sender.sendMessage(_prefix + ChatColor.GREEN + "Your Inventory has been Saved!");
						}
					}
					else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
				}
				else sender.sendMessage(_prefix + ChatColor.RED + "This Option isn't Enabled!");
			}
		}
		else 
		{
			sender.sendMessage(_prefix + "===Commands:===");
			sender.sendMessage(ChatColor.BLUE + "/saveit save" + ChatColor.GREEN + " - Saves All the Configured Worlds, and Inventories (FULLSAVE)");
			sender.sendMessage(ChatColor.BLUE + "/saveit reload" + ChatColor.GREEN + " - Reloads Config");
			sender.sendMessage(ChatColor.BLUE + "/saveit selfsave" + ChatColor.GREEN + " - Saves Your Data Only");
		}
		return false;

	}
  
	public static void WorldSave() {
		// Getting World list.
		ExWorlds = config.getStringList("Worlds");
		// Checking on "EnableSaveMSG".
		if (EnableMsg) {
			Bukkit.getServer().broadcastMessage(colorize(config.getString("SaveMSG")));
		}
		// Getting Worlds, and Saving Them.
		for (World world : Bukkit.getWorlds()) {
			if ((ExWorlds).contains(world.getName())) {
				world.save();
				// Getting All The Players, and Saving Them.
				for (Player player : world.getPlayers()) {
					player.saveData();
				}
				// Full Save On Players
				Bukkit.savePlayers();
			}
			else { 
				// Getting worlds in the config.
				for (String worldname : ExWorlds) {
					//Checking if a world doesn't exist.
					if (Bukkit.getWorld(worldname) == null) {
						sendConsoleMessage(ChatColor.RED + "[ERROR] Not Existing world in config!");
						ExWorlds.remove(worldname);
						sendConsoleMessage(ChatColor.RED + "[ERROR] " + ChatColor.BLUE + worldname + ChatColor.RED + " does not exist! Remove it from the config!");
					}
	    		}
	    	}
	    }
		
	    if (EnableMsg) {
	    	Bukkit.getServer().broadcastMessage(colorize(config.getString("SaveMSG2")));
	    }
	}

	public static void sendConsoleMessage(String msg) {
		// My Nice Colored Console Message Prefix.
		_cs.sendMessage(_prefix + ChatColor.AQUA + msg);
	}

	public static String colorize(String s) {
		// This little code supports coloring.
		if(s == null) return null;
		return s.replaceAll("&([0-9a-f])", "\u00A7$1");
	}
	
	public void ConfigReload() {
		// Getting all the values, then reloading them.
		Delay = config.getInt("DelayInMinutes");
		MSG = config.getString("SaveMSG");
		MSG2 = config.getString("SaveMSG2");
		EnableMsg = config.getBoolean("EnableSaveMSG");
		ExWorlds = config.getStringList("Worlds");
		CheckForUpdates = config.getBoolean("CheckForUpdates");
		DisableDefaultWorldSave = config.getBoolean("DisableDefaultWorldSave");
		SaveOnLogin = config.getBoolean("ExtraOptions.SaveOnLogin");
		SaveOnQuit = config.getBoolean("ExtraOptions.SaveOnQuit");
		SaveOnBlockBreak = config.getBoolean("ExtraOptions.SaveOnBlockBreak");
		SaveOnBlockPlace = config.getBoolean("ExtraOptions.SaveOnBlockPlace");
		SaveOnBlockBreakcount = config.getInt("ExtraOptions.SaveOnBlockBreak.count");
		SaveOnBlockPlacecount = config.getInt("ExtraOptions.SaveOnBlockPlace.count");
		SelfInventorySave = config.getBoolean("ExtraOptions.EnableSelfInventorySave");
		reloadConfig();
		sendConsoleMessage(ChatColor.GREEN + "Config Reloaded!");
	}

}
