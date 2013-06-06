package me.dretax.SaveIt;

import java.io.File;
import java.io.IOException;
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

public class Main extends JavaPlugin
{
	/*
	 * 
	 * @Author: DreTaX
	 * 
	 */
	protected boolean EnableMsg, CheckForUpdates, DisableDefaultWorldSave, SaveOnLogin, SaveOnQuit, SaveOnBlockBreak, SaveOnBlockPlace, SelfInventorySave, SavePlayersFully, Debug, PowerSave, SaveAllWorlds, BroadCastErrorIg;
	protected int Delay, Delay2, SaveOnBlockBreakcount, SaveOnBlockPlacecount, SaveOnLoginCount, SaveOnQuitCount;
	protected PluginManager _pm;
	protected ConsoleCommandSender _cs;
	protected final String _prefix = ChatColor.AQUA + "[SaveIt] ";
    protected List<String> ExWorlds;
    protected Boolean isLatest;
	protected String latestVersion;
	protected final SaveItExpansions expansions = new SaveItExpansions(this);
	protected FileConfiguration config;

    public void onDisable() {
		WorldSaveOnStop();
		if (Debug) {
			sendConsoleMessage(ChatColor.YELLOW + "Saved on Disable!");
		}
		super.onDisable();
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
            if(Debug) {
                sendConsoleMessage(ChatColor.GREEN + "SaveIt Metrics Successfully Enabled!");
            }
        }
        // Couldn't Connect.
        catch (IOException localIOException) {
            if(Debug) {
                sendConsoleMessage(ChatColor.RED + "SaveIt Metrics Failed to boot! Notify DreTaX!");
            }
        }
		/*
		 * Configuration and Command Definitions
		 */
		getCommand("saveit").setExecutor(this);
        config = this.getConfig();
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

        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.saveDefaultConfig();
            if (Debug) {
                sendConsoleMessage(ChatColor.GREEN + "Saved Default Config");
            }
        }
        else {
            if (Debug) {
                sendConsoleMessage(ChatColor.GREEN + "Config Exists");
            }
            CheckConfig();
        }
		/*
		 * Delay
		 */
		Delay = config.getInt("DelayInMinutes");
         Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
             public void run() {
                 if (PowerSave) {
                     for (Player p : getServer().getOnlinePlayers()) {
                         if (p == null) {
                             return;
                         }
                         else {
                             WorldSaveDelayed();
                         }
                     }
                 } else {
                     WorldSaveDelayed();
                 }
             }
         }
         , 1200L * Delay, 1200L * Delay);
		
		/*
		 * Others
		 */
		
		if (DisableDefaultWorldSave) {
			for (World world : Bukkit.getWorlds()) {
				world.setAutoSave(false);
			}
		}
		
		if (CheckForUpdates) {
			SaveItUpdate updateChecker = new SaveItUpdate(this);
			isLatest = updateChecker.isLatest();
			latestVersion = updateChecker.getUpdateVersion();
		}
		
		_pm.registerEvents(this.expansions, this);
		sendConsoleMessage(ChatColor.GREEN + "Successfully Enabled!");
	}

    public void CheckConfig() {
        config = this.getConfig();
        if(!config.contains("ExtraOptions.SaveOnDisable")) {
            config.addDefault("ExtraOptions.SaveOnDisable", true);
            config.options().copyDefaults(true);
            saveConfig();
            reloadConfig();
        }
        if(!config.contains("SaveAllWorlds")) {
            config.addDefault("SaveAllWorlds", false);
            config.options().copyDefaults(true);
            saveConfig();
            reloadConfig();
        }
        if(!config.contains("BroadCastWorldErrorIg"))  {
            config.addDefault("BroadCastWorldErrorIg", false);
            config.options().copyDefaults(true);
            saveConfig();
            reloadConfig();
        }
        if(!config.contains("EnableDelay"))  {
            config.addDefault("EnableDelay", true);
            config.options().copyDefaults(true);
            saveConfig();
            reloadConfig();
        }
    }

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ExWorlds = config.getStringList("Worlds");
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("save")) {
				if (sender.hasPermission("saveit.save")) {
					WorldSaveDelayed();
				}
				else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("saveit.reload")) {
					ConfigReload();
					sender.sendMessage(_prefix + ChatColor.GREEN + "Config Reloaded! Check Console for Errors, If Config doesn't Work");
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
            if (args[0].equalsIgnoreCase("add")) {
                if (sender.hasPermission("saveit.manage")) {
                    ExWorlds = config.getStringList("Worlds");
                    if (args.length == 2) {
                        if(!ExWorlds.contains(args[1])) {
                            ExWorlds.add(args[1]);
                            config.set("Worlds", ExWorlds);
                            saveConfig();
                            ConfigReload();
                            sender.sendMessage(_prefix + ChatColor.GREEN + "Added World: " + args[1]);
                        }
                        else {
                            sender.sendMessage(_prefix + ChatColor.RED + "World Already Exists in config: " + args[1]);
                        }
                    }
                    else sender.sendMessage(_prefix + ChatColor.RED + "Specify a World Name!");
                }
                else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (sender.hasPermission("saveit.manage"))  {
                    ExWorlds = config.getStringList("Worlds");
                    if (args.length == 2) {
                        if(ExWorlds.contains(args[1])) {
                            ExWorlds.remove(args[1]);
                            config.set("Worlds", ExWorlds);
                            saveConfig();
                            ConfigReload();
                            sender.sendMessage(_prefix + ChatColor.GREEN + "Removed World: " + args[1]);
                        }
                        else {
                            sender.sendMessage(_prefix + ChatColor.RED + "World Doesn't Exist in config: " + args[1]);
                        }
                    }
                    else sender.sendMessage(_prefix + ChatColor.RED + "Specify a World Name!");
                }
                else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("saveit.manage"))  {
                    if (!SaveAllWorlds) {
                        ExWorlds = config.getStringList("Worlds");
                        sender.sendMessage(_prefix + ChatColor.GREEN + ExWorlds);
                    }
                    else {
                        sender.sendMessage(_prefix + ChatColor.GREEN + "You are Saving all Existing Worlds.");
                        sender.sendMessage(_prefix + ChatColor.GREEN + "You don't need the list.");
                    }
                }
                else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
            }
		}
		else 
		{
			sender.sendMessage(_prefix + ChatColor.GREEN + "1.0.3 " + ChatColor.AQUA + "===Commands:===");
			sender.sendMessage(ChatColor.BLUE + "/saveit save" + ChatColor.GREEN + " - Saves All the Configured Worlds, and Inventories" + ChatColor.YELLOW +  "(FULLSAVE)");
			sender.sendMessage(ChatColor.BLUE + "/saveit reload" + ChatColor.GREEN + " - Reloads Config");
			sender.sendMessage(ChatColor.BLUE + "/saveit selfsave" + ChatColor.GREEN + " - Saves Your Data Only");
            sender.sendMessage(ChatColor.BLUE + "/saveit add " + ChatColor.YELLOW + "WORLDNAME (Case Sensitive)" + ChatColor.GREEN + " - Adds a Given World to Config");
            sender.sendMessage(ChatColor.BLUE + "/saveit remove " + ChatColor.YELLOW + "WORLDNAME (Case Sensitive)" + ChatColor.GREEN + " - Removes a Given World from Config");
            sender.sendMessage(ChatColor.BLUE + "/saveit list" + ChatColor.GREEN + " - Lists Current Worlds in Config");
		}
		return false;

	}
	
	public void WorldSaveDelayed() {
		// Getting Variables
        config = this.getConfig();
		EnableMsg = config.getBoolean("EnableSaveMSG");
		ExWorlds = config.getStringList("Worlds");
		SavePlayersFully = config.getBoolean("SavePlayersEverywhere");
        SaveAllWorlds = config.getBoolean("SaveAllWorlds");
        BroadCastErrorIg = config.getBoolean("BroadCastWorldErrorIg");
		Delay2 = 1;
		// Checking on "EnableSaveMSG".
		if (EnableMsg) {
			Bukkit.getServer().broadcastMessage(colorize(config.getString("SaveMSG")));
		}
		
		
		/* Full Save On Players, if Enabled
		 * If not, It will only Save Players in
		 * The Configured Worlds
		 */		
		if (SavePlayersFully) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable()
			{
	               public void run() {
	                   Delay2 += 1;
	                   Bukkit.savePlayers();
	               }
			}
			, 20L * Delay2);
		}
			
		// Getting Worlds, and Saving Them.
		for (final World world : Bukkit.getWorlds()) {
            String w = world.getName();
            if (!SaveAllWorlds) {
			    // Checking if an Existing World is written in the Config
                if ((ExWorlds).contains(w)) {
                    for (String worldname : ExWorlds) {
                        if (Bukkit.getWorld(worldname) != null) {
				            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                            public void run() {
                                    Delay2 += 1;
                                    world.save();
                                    // Getting All The Players, and Saving Them, only in the Configured Worlds.
                                    if (!SavePlayersFully) {
                                        for (Player player : world.getPlayers()) {
                                            player.saveData();
                                        }
                                    }
                                }
                            }
                            , 20L * Delay2);
                        }
                        if (Bukkit.getWorld(worldname) == null) {
                            sendConsoleMessage(ChatColor.RED + "[ERROR] Not Existing World in Config!");
                            sendConsoleMessage(ChatColor.RED + "[ERROR] " + ChatColor.BLUE + worldname + ChatColor.RED + " does not exist! Remove it from the config!");
                            if (BroadCastErrorIg) {
                                Bukkit.getServer().broadcastMessage(_prefix + ChatColor.RED + "[ERROR] Not Existing World In Config!");
                                Bukkit.getServer().broadcastMessage(_prefix + ChatColor.RED + "[ERROR] " + ChatColor.BLUE + worldname + ChatColor.RED + " does not exist! Remove it from the config!");
                            }
                        }
                    }
			    }
            }
            /* If SaveAllWorlds is true
		    * We will Save all the worlds instead of the configured one
		    * Also Calling Performance Method here
		    */
            else {
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    public void run() {
                        Delay2 += 1;
                        world.save();
                        // Getting All The Players, and Saving Them, only in the Configured Worlds.
                        if (!SavePlayersFully) {
                            for (Player player : world.getPlayers()) {
                                player.saveData();
                            }
                        }
                    }
                }
                , 20L * Delay2);
            }
        }
		
	    if (EnableMsg) {
	    	Bukkit.getServer().broadcastMessage(colorize(config.getString("SaveMSG2")));
	    }
	}
	
	public void WorldSaveOnStop() {
		ExWorlds = config.getStringList("Worlds");
		SavePlayersFully = config.getBoolean("SavePlayersEverywhere");
		
		if (SavePlayersFully) {
			Bukkit.savePlayers();
		}
		
		for (World world : Bukkit.getWorlds()) {
			if ((ExWorlds).contains(world.getName())) {
				world.save();
				if (!SavePlayersFully) {	
					for (Player player : world.getPlayers()) {
						player.saveData();
					}
				}
			}
	    }
	}

	public void sendConsoleMessage(String msg) {
		// My Nice Colored Console Message Prefix.
		_cs.sendMessage(_prefix + ChatColor.AQUA + msg);
	}

	public String colorize(String s) {
		// This little code supports coloring.
		// If String is null it will return null
		if(s == null) return null;
		// Extra Stuff, taken from My SimpleNames Plugin
		s = s.replaceAll("&r", ChatColor.RESET + "");
		s = s.replaceAll("&l", ChatColor.BOLD + "");
		s = s.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
		s = s.replaceAll("&o", ChatColor.ITALIC + "");
		s = s.replaceAll("&n", ChatColor.UNDERLINE + "");
		//This one Supports all the Default Colors
		return s.replaceAll("&([0-9a-f])", "\u00A7$1");
	}
	
	public void ConfigReload() {
		this.reloadConfig();
		config = this.getConfig();
		// Getting all the values, then reloading them.
		EnableMsg = config.getBoolean("EnableSaveMSG");
		CheckForUpdates = config.getBoolean("CheckForUpdates");
		SavePlayersFully = config.getBoolean("SavePlayersEverywhere");
		DisableDefaultWorldSave = config.getBoolean("DisableDefaultWorldSave");
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
		Delay = config.getInt("DelayInMinutes");
		PowerSave = config.getBoolean("EnablePowerSave");
        SaveAllWorlds = config.getBoolean("SaveAllWorlds");
        BroadCastErrorIg = config.getBoolean("BroadCastWorldErrorIg");
		this.reloadConfig();
		if (Debug) {
			sendConsoleMessage(ChatColor.GREEN + "Config Reloaded!");
		}
	}

}
