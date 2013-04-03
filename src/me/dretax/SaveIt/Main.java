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
	public int Delay;
	public boolean Worldd;
	public static boolean EnableMsg;
	public String MSG;
	public String MSG2;
	public PluginManager _pm;
	public static ConsoleCommandSender _cs;
	public static final String _prefix = ChatColor.AQUA + "[SaveIt] ";
	private List<String> ExWorlds = Arrays.asList(new String[] { "world", "world_nether", "world_the_end"});
	private FileConfiguration config;
	public Boolean isLatest;
	public String latestVersion;
	public Main plugin;
	Logger log = Logger.getLogger("Minecraft");
	
	public void onDisable()
	{
		super.onDisable();
	}
	
  
	public void onEnable() {
		this._pm = getServer().getPluginManager();
		_cs = getServer().getConsoleSender();
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
			sendConsoleMessage(ChatColor.GREEN + "SaveIt Metrics Successfully Enabled!");
		}
		catch (IOException localIOException) {
		}
		getCommand("saveit").setExecutor(this);
		config = this.getConfig();
		config.addDefault("DelayInMinutes", Integer.valueOf(10));
		config.addDefault("Worlds", ExWorlds);
		config.addDefault("EnableSaveMSG", true);
		config.addDefault("SaveMSG", "&aStarting world save...");
		config.addDefault("SaveMSG2", "&aWorld save completed!");
		config.options().copyDefaults(true);
		saveConfig();
		EnableMsg = getConfig().getBoolean("EnableSaveMSG");
		int delay = getConfig().getInt("DelayInMinutes");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			public void run() {
				WorldSave();
			}
		}
		, 1200L * delay, 1200L * delay);
		SaveItUpdate updateChecker = new SaveItUpdate(this);
		this.isLatest = updateChecker.isLatest();
	    this.latestVersion = updateChecker.getUpdateVersion();
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
		}
		else 
		{
			sender.sendMessage(_prefix + "===Commands:===");
			sender.sendMessage(ChatColor.BLUE + "/saveit save" + ChatColor.GREEN + " - Saves All the Configured Worlds");
			sender.sendMessage(ChatColor.BLUE + "/saveit reload" + ChatColor.GREEN + " - Reloads Config");
		}
		return false;
			
	}
  
	public void WorldSave(){
		this.ExWorlds = config.getStringList("Worlds");
		if (EnableMsg) {
			Bukkit.getServer().broadcastMessage(colorize(config.getString("SaveMSG")));
		}
		for (World world : Bukkit.getServer().getWorlds()) {
			if ((this.ExWorlds).contains(world.getName())) {
				world.save();
				for (Player player : world.getPlayers()) {
					player.saveData();
				}
			} else { 
				sendConsoleMessage(ChatColor.RED + "[ERROR] Not Existing world in config!");
				for(String worldname : ExWorlds) {
					if (Bukkit.getWorld(worldname) == null) {
						ExWorlds.remove(worldname);
						sendConsoleMessage(ChatColor.RED + worldname + ChatColor.BLUE + " does not exist! Remove it from the config!");
					}
	    		}
	    	}
	    }
		
	    if (EnableMsg) {
	    	Bukkit.getServer().broadcastMessage(colorize(config.getString("SaveMSG2")));
	    }
	}

	public static void sendConsoleMessage(String msg) {
		_cs.sendMessage(_prefix + ChatColor.AQUA + msg);
	}

	public static String colorize(String s){
	    if(s == null) return null;
	    return s.replaceAll("&([0-9a-f])", "\u00A7$1");
	}
	
	public void ConfigReload() {
		Delay = config.getInt("DelayInMinutes");
		MSG = config.getString("SaveMSG");
		MSG2 = config.getString("SaveMSG2");
		EnableMsg = config.getBoolean("EnableSaveMSG");
		ExWorlds = config.getStringList("Worlds");
		Main.this.reloadConfig();
		sendConsoleMessage(ChatColor.GREEN + "Config Reloaded!");
	}
}
