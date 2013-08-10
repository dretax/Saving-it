package me.dretax.SaveIt;

import java.io.IOException;
import java.util.Date;

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
	private int Delay, Delay2;
	protected PluginManager _pm;
	protected ConsoleCommandSender _cs;
	protected String _prefix = ChatColor.AQUA + "[SaveIt] ";
    protected Boolean isLatest;
	protected String latestVersion;
    private SaveItConfig SaveItConfig = new SaveItConfig(this);
    private SaveItExpansions expansions = new SaveItExpansions(this, SaveItConfig);
    private BackUp backup = new BackUp(this, SaveItConfig);

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
        SaveItConfig.load();
        Checkv();
        backup.check();
        backup.kcheck();
		this._pm = getServer().getPluginManager();
		_cs = getServer().getConsoleSender();
        if (SaveItConfig.EnableBackup) {
            if (SaveItConfig.AutoBackup) {
                if ((SaveItConfig.Decide).equalsIgnoreCase("INTERVAL")) {
                    long t = (long) (72000 * SaveItConfig.intv);
                    if (t > 0) {
                        long delay = SaveItConfig.StartOnAGivenHour != null ? s(SaveItConfig.StartOnAGivenHour) : t;
                        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                            @Override
                            public void run() {
                                if (SaveItConfig.PowerSave) {
                                    int players = Bukkit.getServer().getOnlinePlayers().length;
                                    if (players != 0)  {
                                        backup.backupdir();
                                    }
                                }
                                else {
                                    backup.backupdir();
                                }
                            }
                        }, delay, t);
                    }
                }
                if ((SaveItConfig.Decide).equalsIgnoreCase("DAY")) {
                    Bukkit.getScheduler().runTaskTimer(this, new Runnable()
                    {
                        public void run() {
                            backup.kcheck();
                        }
                    }
                    , 1200L * 30, 1200L * 30);
                }
            }
        }
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
                WorldSaveDelayed();
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
                        SaveItConfig.load();
                        if(!SaveItConfig.ExWorlds.contains(args[1])) {
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
                        SaveItConfig.load();
                        if(SaveItConfig.ExWorlds.contains(args[1])) {
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
                    if (!SaveItConfig.SaveAllWorlds) {
                        SaveItConfig.config = getConfig();
                        SaveItConfig.load();
                        sender.sendMessage(_prefix + ChatColor.GREEN + SaveItConfig.ExWorlds);
                    }
                    else {
                        sender.sendMessage(_prefix + ChatColor.GREEN + "You are Saving all Existing Worlds.");
                        sender.sendMessage(_prefix + ChatColor.GREEN + "You don't need the list.");
                    }
                }
                else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
            }
            if (args[0].equalsIgnoreCase("backup"))  {
                if( sender.hasPermission("saveit.backup")) {
                    if (SaveItConfig.EnableBackup) {
                        sender.sendMessage(_prefix + ChatColor.GREEN + "StandBy...");
                        backup.backupdir();
                    }
                    else sender.sendMessage(_prefix + ChatColor.RED + "Backup Mode isn't Enabled!");

                }
                else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
            }
		}
		else 
		{
			sender.sendMessage(_prefix + ChatColor.GREEN + "1.0.7.6 " + ChatColor.AQUA + "===Commands:===");
			sender.sendMessage(ChatColor.BLUE + "/saveit save" + ChatColor.GREEN + " - Saves All the Configured Worlds, and Inventories" + ChatColor.YELLOW +  "(FULLSAVE)");
			sender.sendMessage(ChatColor.BLUE + "/saveit reload" + ChatColor.GREEN + " - Reloads Config");
			sender.sendMessage(ChatColor.BLUE + "/saveit selfsave" + ChatColor.GREEN + " - Saves Your Data Only");
            sender.sendMessage(ChatColor.BLUE + "/saveit add " + ChatColor.YELLOW + "WORLDNAME (Case Sensitive)" + ChatColor.GREEN + " - Adds a Given World to Config");
            sender.sendMessage(ChatColor.BLUE + "/saveit remove " + ChatColor.YELLOW + "WORLDNAME (Case Sensitive)" + ChatColor.GREEN + " - Removes a Given World from Config");
            sender.sendMessage(ChatColor.BLUE + "/saveit list" + ChatColor.GREEN + " - Lists Current Worlds in Config");
            sender.sendMessage(ChatColor.BLUE + "/saveit backup" + ChatColor.GREEN + " - Creates a Zip of all your Server Folders (BETA)");
		}
		return false;

	}
	
	protected void WorldSaveDelayed() {
		// Getting Variables
        SaveItConfig.config = getConfig();
        SaveItConfig.EnableMsg = SaveItConfig.config.getBoolean("EnableSaveMSG");
        SaveItConfig.SavePlayersFully = SaveItConfig.config.getBoolean("SavePlayersEverywhere");
        SaveItConfig.PowerSave = SaveItConfig.config.getBoolean("EnablePowerSave");
        SaveItConfig.SaveAllWorlds = SaveItConfig.config.getBoolean("SaveAllWorlds");
        SaveItConfig.BroadCastErrorIg = SaveItConfig.config.getBoolean("BroadCastWorldErrorIg");

        if (SaveItConfig.PowerSave) {
            int players = this.getServer().getOnlinePlayers().length;
            if (players == 0)  {
                return;
            }
        }
		Delay2 = 1;
		// Checking on "EnableSaveMSG".
		if (SaveItConfig.EnableMsg) {
			Bukkit.getServer().broadcastMessage(colorize(SaveItConfig.config.getString("SaveMSG")));
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
	    	Bukkit.getServer().broadcastMessage(colorize(SaveItConfig.config.getString("SaveMSG2")));
	    }
	}
	
	private void WorldSaveOnStop() {
        SaveItConfig.load();

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

    private void Checkv() {
        if (!SaveItConfig.config.contains("BroadCastWorldErrorIg")) {
            SaveItConfig.config.set("BroadCastWorldErrorIg", false);
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.EnableBackup")) {
            SaveItConfig.config.set("BackUp.EnableBackup", false);
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.EnableBackupMSG")) {
            SaveItConfig.config.set("BackUp.EnableBackupMSG", true);
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.WarningMSG")) {
            SaveItConfig.config.set("BackUp.WarningMSG", "&2Warning! Backup has been executed!");
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.WarningMSG2")) {
            SaveItConfig.config.set("BackUp.WarningMSG2", "&aBackup Finished!");
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.BackupHoursInterval")) {
            SaveItConfig.config.set("BackUp.BackupHoursInterval", 1.0);
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.EnableAutoBackup")) {
            SaveItConfig.config.set("BackUp.EnableAutoBackup", false);
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.EnablePlayerKickWhileBackup")) {
            SaveItConfig.config.set("BackUp.EnablePlayerKickWhileBackup", false);
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.IntervalOrDay")) {
            SaveItConfig.config.set("BackUp.IntervalOrDay", "INTERVAL");
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.KickBackupMSG")) {
            SaveItConfig.config.set("BackUp.KickBackupMSG", "Server Is maing a Backup file..");
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.Date")) {
            SaveItConfig.config.set("BackUp.Date", 0);
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SaveItConfig.config.contains("BackUp.DateDayDelay")) {
            SaveItConfig.config.set("BackUp.DateDayDelay", 7);
            try {
                SaveItConfig.config.save(SaveItConfig.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	private void sendConsoleMessage(String msg) {
		// My Nice Colored Console Message Prefix.
		_cs.sendMessage(_prefix + ChatColor.AQUA + msg);
	}

	private String colorize(String s) {
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
	
	private void ConfigReload() {
        SaveItConfig.config = getConfig();
        SaveItConfig.load();
        Delay = SaveItConfig.config.getInt("DelayInMinutes");
		if (SaveItConfig.Debug) {
			sendConsoleMessage(ChatColor.GREEN + "Config Reloaded!");
		}
	}

    @SuppressWarnings("unused")
    private long s(double s) {
        double n = h(new Date());
        double d = n - SaveItConfig.StartOnAGivenHour;
        if (d < 0) {
            d += 24;
        }
        double ip = d - Math.floor(d / SaveItConfig.intv) * SaveItConfig.intv;
        double r = SaveItConfig.intv - ip;
        return (long) (r * 72000);
    }

    @SuppressWarnings("deprecation")
    protected double h(Date t) {
        return t.getHours() + t.getMinutes() / 60. + t.getSeconds() / 3600.;
    }
}
