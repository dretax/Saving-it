package me.dretax.SaveIt;

import java.io.IOException;
import me.dretax.SaveIt.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
	protected int Delay, Delay2;
	protected PluginManager _pm;
	protected ConsoleCommandSender _cs;
	protected String _prefix = ChatColor.AQUA + "[SaveIt] ";
    protected Boolean isLatest;
	protected String latestVersion;
    protected SaveItExpansions expansions= new SaveItExpansions(this);

    public void onDisable() {
        if (SaveItConfig.SaveOnDisable) {
		    WorldSaveOnStop();
            if (SaveItConfig.Debug) {
                sendConsoleMessage(ChatColor.YELLOW + "Saved on Disable!");
            }
        }
		super.onDisable();
	}
	
  
	public void onEnable() {
        SaveItConfig.create();
		this._pm = getServer().getPluginManager();
		_cs = getServer().getConsoleSender();
		/*
		 * Metrics
		 */
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
            if(SaveItConfig.Debug) {
                sendConsoleMessage(ChatColor.GREEN + "SaveIt Metrics Successfully Enabled!");
            }
        }
        // Couldn't Connect.
        catch (IOException localIOException) {
            if(SaveItConfig.Debug) {
                sendConsoleMessage(ChatColor.RED + "SaveIt Metrics Failed to boot! Notify DreTaX!");
            }
        }
		getCommand("saveit").setExecutor(this);

		/*
		 * Delay
		 */
		
		Delay = SaveItConfig.config.getInt("DelayInMinutes");
		
		Bukkit.getScheduler().runTaskTimer(this, new Runnable()
		{
			public void run() {
				if (SaveItConfig.PowerSave) {
					for(Player p : getServer().getOnlinePlayers()) {
						if (p == null) {
							return;
						}
						else {
							WorldSaveDelayed();
						}
					}
				}
				else {
					WorldSaveDelayed();
				}
				
			}
		}
		, 1200L * Delay, 1200L * Delay);
		
		
		/*
		 * Others
		 */
		
		if (SaveItConfig.DisableDefaultWorldSave) {
			for (World world : Bukkit.getWorlds()) {
				world.setAutoSave(false);
			}
		}
		
		if (SaveItConfig.CheckForUpdates) {
			SaveItUpdate updateChecker = new SaveItUpdate(this);
			isLatest = updateChecker.isLatest();
			latestVersion = updateChecker.getUpdateVersion();
		}
		
		_pm.registerEvents(this.expansions, this);
		sendConsoleMessage(ChatColor.GREEN + "Successfully Enabled!");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("save")) {
				if (sender.hasPermission("saveit.save")) {
                    SaveItConfig.config = getConfig();
                    SaveItConfig.ExWorlds = SaveItConfig.config.getStringList("Worlds");
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
				if (SaveItConfig.SelfInventorySave) {
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
                    if (args.length == 2) {
                        SaveItConfig.config = getConfig();
                        if(!SaveItConfig.ExWorlds.contains(args[1])) {
                            SaveItConfig.load();
                            SaveItConfig.ExWorlds = SaveItConfig.config.getStringList("Worlds");
                            SaveItConfig.ExWorlds.add(args[1]);
                            SaveItConfig.config.set("Worlds", SaveItConfig.ExWorlds);
                            try {
                                SaveItConfig.config.save(SaveItConfig.configFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                    if (args.length == 2) {
                        SaveItConfig.config = getConfig();
                        if(SaveItConfig.ExWorlds.contains(args[1])) {
                            SaveItConfig.load();
                            SaveItConfig.ExWorlds = SaveItConfig.config.getStringList("Worlds");
                            SaveItConfig.ExWorlds.remove(args[1]);
                            SaveItConfig.config.set("Worlds", SaveItConfig.ExWorlds);
                            try {
                                SaveItConfig.config.save(SaveItConfig.configFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                    SaveItConfig.config = getConfig();
                    if (!SaveItConfig.SaveAllWorlds) {
                        SaveItConfig.load();
                        SaveItConfig.ExWorlds = SaveItConfig.config.getStringList("Worlds");
                        sender.sendMessage(_prefix + ChatColor.GREEN + SaveItConfig.ExWorlds);
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
			sender.sendMessage(_prefix + ChatColor.GREEN + "1.0.7 " + ChatColor.AQUA + "===Commands:===");
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
        SaveItConfig.config = getConfig();
        SaveItConfig.EnableMsg = SaveItConfig.config.getBoolean("EnableSaveMSG");
        SaveItConfig.ExWorlds = SaveItConfig.config.getStringList("Worlds");
        SaveItConfig.SavePlayersFully = SaveItConfig.config.getBoolean("SavePlayersEverywhere");
        SaveItConfig.SaveAllWorlds = SaveItConfig.config.getBoolean("SaveAllWorlds");
        SaveItConfig.BroadCastErrorIg = SaveItConfig.config.getBoolean("BroadCastWorldErrorIg");
        String msg = SaveItConfig.config.getString("SaveMSG");
        String msg2 = SaveItConfig.config.getString("SaveMSG2");
		Delay2 = 1;
		// Checking on "EnableSaveMSG".
		if (SaveItConfig.EnableMsg) {
			Bukkit.getServer().broadcastMessage(colorize(msg));
		}
		
		
		/* Full Save On Players, if Enabled
		 * If not, It will only Save Players in
		 * The Configured Worlds
		 */		
		if (SaveItConfig.SavePlayersFully) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable()
			{
	               public void run() {
	                   Delay2 += 1;
	                   Bukkit.savePlayers();
	               }
			}
			, 20L * Delay2);
		}
        if (!SaveItConfig.SaveAllWorlds) {
		    // Checking if an Existing World is written in the Config
            for (final String worldname : SaveItConfig.ExWorlds) {
                if (Bukkit.getWorld(worldname) != null) {
				    Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                        public void run() {
                        Delay2 += 1;
                        Bukkit.getWorld(worldname).save();
                        // Getting All The Players, and Saving Them, only in the Configured Worlds.
                        if (!SaveItConfig.SavePlayersFully) {
                            for (Player player : Bukkit.getWorld(worldname).getPlayers()) {
                                player.saveData();
                            }
                        }
                        }
                    }
                    , 20L * Delay2);
                }
                else {
                    sendConsoleMessage(ChatColor.RED + "[ERROR] Not Existing World in Config!");
                    sendConsoleMessage(ChatColor.RED + "[ERROR] " + ChatColor.BLUE + worldname + ChatColor.RED + " does not exist! Remove it from the config!");
                    if (SaveItConfig.BroadCastErrorIg) {
                        Bukkit.getServer().broadcastMessage(_prefix + ChatColor.RED + "[ERROR] Not Existing World In Config!");
                        Bukkit.getServer().broadcastMessage(_prefix + ChatColor.RED + "[ERROR] " + ChatColor.BLUE + worldname + ChatColor.RED + " does not exist! Remove it from the config!");
                    }
                }
            }
        }
        /* If SaveAllWorlds is true
		* We will Save all the worlds instead of the configured one
		* Also Calling Performance Method here
		*/
        else {
            // Getting Worlds, and Saving Them.
            for (final World world : Bukkit.getWorlds()) {
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    public void run() {
                        Delay2 += 1;
                        world.save();
                        // Getting All The Players, and Saving Them, only in the Configured Worlds.
                        if (!SaveItConfig.SavePlayersFully) {
                            for (Player player : world.getPlayers()) {
                                player.saveData();
                            }
                        }
                    }
                }
                , 20L * Delay2);
            }
        }
		
	    if (SaveItConfig.EnableMsg) {
	    	Bukkit.getServer().broadcastMessage(colorize(msg2));
	    }
	}
	
	public void WorldSaveOnStop() {
        SaveItConfig.config = getConfig();
        SaveItConfig.ExWorlds = SaveItConfig.config.getStringList("Worlds");
        SaveItConfig.SavePlayersFully = SaveItConfig.config.getBoolean("SavePlayersEverywhere");
		
		if (SaveItConfig.SavePlayersFully) {
			Bukkit.savePlayers();
		}
		
		for (World world : Bukkit.getWorlds()) {
            if (!SaveItConfig.SaveAllWorlds) {
			    if ((SaveItConfig.ExWorlds).contains(world.getName())) {
				    world.save();
				    if (!SaveItConfig.SavePlayersFully) {
					    for (Player player : world.getPlayers()) {
						    player.saveData();
					    }
				    }
			    }
            }
            else {
                world.save();
                if (!SaveItConfig.SavePlayersFully) {
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
        SaveItConfig.config = getConfig();
        SaveItConfig.load();
        Delay = SaveItConfig.config.getInt("DelayInMinutes");
		if (SaveItConfig.Debug) {
			sendConsoleMessage(ChatColor.GREEN + "Config Reloaded!");
		}
	}

}
