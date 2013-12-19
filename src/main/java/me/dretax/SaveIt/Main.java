package me.dretax.SaveIt;

import me.dretax.SaveIt.metrics.Metrics;
import me.dretax.SaveIt.tasks.SaveItTaskManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {
	/*
	 *
	 * @Author: DreTaX
	 *
	 */
	public int Delay;
	public ConsoleCommandSender _cs;
	public String _prefix = ChatColor.AQUA + "[SaveIt] ";
	PluginManager _pm;
	private SaveItConfig SaveItConfig;
	private SaveItExpansions expansions;
	private BackUp backup;
	private SaveItTaskManager manager;
	private boolean update = false;
	private static Main instance;

	public void onDisable() {
		super.onDisable();
	}

	public void onEnable() {
		instance = this;
		SaveItConfig = new SaveItConfig();
		expansions = new SaveItExpansions();
		backup = new BackUp();
		manager = new SaveItTaskManager();
		getSaveItConfig().create();
		this._pm = getServer().getPluginManager();
		_cs = getServer().getConsoleSender();
		if (getSaveItConfig().EnableBackup) {
			getSaveItBackup().check();
			getSaveItBackup().kcheck();
			getSaveItBackup().delZip();
			if (getSaveItConfig().AutoBackup) {
				if ((getSaveItConfig().Decide).equalsIgnoreCase("INTERVAL")) {
					long t = (long) (72000 * getSaveItConfig().intv);
					if (t > 0) {
						getSaveItManager().StartIntervalBackup();
					}
				}
				else if ((getSaveItConfig().Decide).equalsIgnoreCase("DAY")) {
					getSaveItManager().StartDailyBackup();
				}
			}
			if (getSaveItConfig().PurgeBackups) {
				getSaveItManager().StartPurge();
			}
		}
		/*
		 * Metrics
		 */
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
			if (getSaveItConfig().Debug) sendConsoleMessage(ChatColor.GREEN + "SaveIt Metrics Successfully Enabled!");
		}
		// Couldn't Connect.
		catch (IOException localIOException) {
			if (getSaveItConfig().Debug) sendConsoleMessage(ChatColor.RED + "SaveIt Metrics Failed to boot! Notify DreTaX!");
		}
		getCommand("saveit").setExecutor(this);

		/*
		 * Delay
		 */

		Delay = getSaveItConfig().getPluginConfig().getInt("DelayInMinutes");

		getSaveItManager().StartAutoSave();
		/*
		 * Others
		 */

		if (getSaveItConfig().DisableDefaultWorldSave) {
			FileConfiguration bukkit = YamlConfiguration.loadConfiguration(new File(getServer().getWorldContainer(), "bukkit.yml"));
			File fb = new File("bukkit.yml");
			bukkit.set("ticks-per.autosave", 0);
			try {
				bukkit.save(fb);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (getSaveItConfig().CheckForUpdates) {
			sendConsoleMessage(ChatColor.GREEN + "Checking for updates.....");
			SaveItUpdate saveItUpdate = new SaveItUpdate(this, 33841, this.getFile(), SaveItUpdate.UpdateType.NO_DOWNLOAD, false);
			update = saveItUpdate.getResult() == SaveItUpdate.UpdateResult.UPDATE_AVAILABLE;
			if (update) {
				sendConsoleMessage(ChatColor.GREEN + "New Update Available! Version: " + ChatColor.RED + saveItUpdate.getLatestName());
				sendConsoleMessage(ChatColor.GREEN + "Your Version: " + _pm.getPlugin("SaveIt").getDescription().getVersion());
				sendConsoleMessage(ChatColor.GREEN + "Download at: http://dev.bukkit.org/bukkit-plugins/automatically-world-saving/");
				sendConsoleMessage(ChatColor.GREEN + "Or simply type /saveit update to update it automatically");
			} else {
				sendConsoleMessage(ChatColor.GREEN + "No updates available! You are cool :D!");
			}
		}

		if (getSaveItConfig().SaveOnLogin || getSaveItConfig().SaveOnQuit || getSaveItConfig().SaveOnBlockBreak || getSaveItConfig().SaveOnBlockPlace) {
			_pm.registerEvents(this.expansions, this);
		}
		sendConsoleMessage(ChatColor.GREEN + "Successfully Enabled!");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("save")) {
				if (sender.hasPermission("saveit.save")) {
					WorldSaveDelayed();
				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			else if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("saveit.reload")) {
					ConfigReload();
					sender.sendMessage(_prefix + ChatColor.GREEN + "Config Reloaded! Check Console for Errors, If Config doesn't Work");
				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			else if (args[0].equalsIgnoreCase("selfsave")) {
				if (getSaveItConfig().SelfInventorySave) {
					if (sender.hasPermission("saveit.selfsave")) {
						if (!(sender instanceof Player)) {
							sender.sendMessage(_prefix + ChatColor.GREEN + "This command can only be run by a player.");
						} else {
							((Player) sender).saveData();
							sender.sendMessage(_prefix + ChatColor.GREEN + "Your Inventory has been Saved!");
						}
					} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
				} else sender.sendMessage(_prefix + ChatColor.RED + "This Option isn't Enabled!");
			}
			else if (args[0].equalsIgnoreCase("add")) {
				if (sender.hasPermission("saveit.manage")) {
					if (args.length == 2) {
						if (getSaveItConfig().SaveAllWorlds) {
							sender.sendMessage(_prefix + ChatColor.GREEN + "You are Saving all Existing Worlds.");
							sender.sendMessage(_prefix + ChatColor.GREEN + "You don't need this.");
							return false;
						}
						getSaveItConfig().load();
						if (!getSaveItConfig().ExWorlds.contains(args[1])) {
							getSaveItConfig().ExWorlds.add(args[1]);
							getSaveItConfig().getPluginConfig().set("Worlds", getSaveItConfig().ExWorlds);
							try {
								getSaveItConfig().getPluginConfig().save(getSaveItConfig().configFile);
							} catch (Exception e) {
								e.printStackTrace();
							}
							ConfigReload();
							sender.sendMessage(_prefix + ChatColor.GREEN + "Added World: " + args[1]);
						} else {
							sender.sendMessage(_prefix + ChatColor.RED + "World Already Exists in config: " + args[1]);
						}
					} else sender.sendMessage(_prefix + ChatColor.RED + "Specify a World Name!");
				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			else if (args[0].equalsIgnoreCase("remove")) {
				if (sender.hasPermission("saveit.manage")) {
					if (args.length == 2) {
						if (getSaveItConfig().SaveAllWorlds) {
							sender.sendMessage(_prefix + ChatColor.GREEN + "You are Saving all Existing Worlds.");
							sender.sendMessage(_prefix + ChatColor.GREEN + "You don't need this.");
							return false;
						}
						getSaveItConfig().load();
						if (getSaveItConfig().ExWorlds.contains(args[1])) {
							getSaveItConfig().ExWorlds.remove(args[1]);
							getSaveItConfig().getPluginConfig().set("Worlds", getSaveItConfig().ExWorlds);
							try {
								getSaveItConfig().getPluginConfig().save(getSaveItConfig().configFile);
							} catch (Exception e) {
								e.printStackTrace();
							}
							ConfigReload();
							sender.sendMessage(_prefix + ChatColor.GREEN + "Removed World: " + args[1]);
						} else {
							sender.sendMessage(_prefix + ChatColor.RED + "World Doesn't Exist in config: " + args[1]);
						}
					} else sender.sendMessage(_prefix + ChatColor.RED + "Specify a World Name!");
				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			else if (args[0].equalsIgnoreCase("list")) {
				if (sender.hasPermission("saveit.manage")) {
					if (!getSaveItConfig().SaveAllWorlds) {
						getSaveItConfig().load();
						sender.sendMessage(_prefix + ChatColor.GREEN + getSaveItConfig().ExWorlds);
					} else {
						sender.sendMessage(_prefix + ChatColor.GREEN + "You are Saving all Existing Worlds.");
						sender.sendMessage(_prefix + ChatColor.GREEN + "You don't need the list.");
					}
				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			else if (args[0].equalsIgnoreCase("backup")) {
				if (sender.hasPermission("saveit.backup")) {
					if (getSaveItConfig().EnableBackup) {
						backup.delZip();
						sender.sendMessage(_prefix + ChatColor.GREEN + "StandBy, backup starts in 5 seconds...");
						getServer().getScheduler().runTaskLater(this, new Runnable() {
							public void run() {
								backup.backupdir();
							}
						}
						, 20L * 5);
					} else sender.sendMessage(_prefix + ChatColor.RED + "Backup Mode isn't Enabled!");

				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			else if (args[0].equalsIgnoreCase("update")) {
				if (sender.hasPermission("saveit.manage")) {
					sender.sendMessage(_prefix + ChatColor.GREEN + "Updating...");
					SaveItUpdate saveItUpdate = new SaveItUpdate(this, 33841, this.getFile(), SaveItUpdate.UpdateType.NO_DOWNLOAD, false);
					update = saveItUpdate.getResult() == SaveItUpdate.UpdateResult.UPDATE_AVAILABLE;
					if (update) {
						SaveItUpdate saveItUpdate2 = new SaveItUpdate(this, 33841, this.getFile(), SaveItUpdate.UpdateType.NO_VERSION_CHECK, true);
						update = saveItUpdate2.getResult() == SaveItUpdate.UpdateResult.SUCCESS;
						if (update) {
							sender.sendMessage(_prefix + ChatColor.GREEN + "Success! Restart or Reload to make changes!");
						} else {
							sender.sendMessage(_prefix + ChatColor.RED + "Update failed, check console!");
						}
					}
					else {
						sender.sendMessage(_prefix + ChatColor.RED + "You already have the latest version!");
					}
				}
			}
		} else {
			sender.sendMessage(_prefix + ChatColor.GREEN + "1.1.6 " + ChatColor.AQUA + "===Commands:===");
			sender.sendMessage(ChatColor.BLUE + "/saveit save" + ChatColor.GREEN + " - Saves All the Configured Worlds, and Inventories" + ChatColor.YELLOW + "(FULLSAVE)");
			sender.sendMessage(ChatColor.BLUE + "/saveit reload" + ChatColor.GREEN + " - Reloads Config");
			sender.sendMessage(ChatColor.BLUE + "/saveit selfsave" + ChatColor.GREEN + " - Saves Your Data Only");
			sender.sendMessage(ChatColor.BLUE + "/saveit add " + ChatColor.YELLOW + "WORLDNAME (Case Sensitive)" + ChatColor.GREEN + " - Adds a Given World to Config");
			sender.sendMessage(ChatColor.BLUE + "/saveit remove " + ChatColor.YELLOW + "WORLDNAME (Case Sensitive)" + ChatColor.GREEN + " - Removes a Given World from Config");
			sender.sendMessage(ChatColor.BLUE + "/saveit list" + ChatColor.GREEN + " - Lists Current Worlds in Config");
			sender.sendMessage(ChatColor.BLUE + "/saveit backup" + ChatColor.GREEN + " - Creates a Zip of all your Server Folders");
			sender.sendMessage(ChatColor.BLUE + "/saveit update" + ChatColor.GREEN + " - Download the Latest Update");
		}
		return false;

	}

	public void WorldSaveDelayed() {
		if (getSaveItConfig().PowerSave) {
			sendConsoleMessage("PowerSave is enabled");
			int players = getServer().getOnlinePlayers().length;
			if (players == 0) {
				return;
			}
		}
		getSaveItManager().Delay2 = 1;
		// Checking on "EnableSaveMSG".
		if (getSaveItConfig().EnableMsg) getServer().broadcastMessage(colorize(getSaveItConfig().getPluginConfig().getString("SaveMSG")));

		/** Full Save On Players, if Enabled
		 * If not, It will only Save Players in
		 * The Configured Worlds
		 */
		if (getSaveItConfig().SavePlayersFully) {
			getSaveItManager().StartSavingAllPlayers();
		}

		if (!getSaveItConfig().SaveAllWorlds) {
			getSaveItManager().StartSmoothingonCustomWorlds();
		}
	   /** If SaveAllWorlds is true
		* We will Save all the worlds instead of the configured one
		* Also Calling Performance Method here
		*/
		if (getSaveItConfig().SaveAllWorlds) {
			getSaveItManager().StartSmoothingOnAllWorlds();
		}

		if (getSaveItConfig().EnableMsg) getServer().broadcastMessage(colorize(getSaveItConfig().getPluginConfig().getString("SaveMSG2")));
	}

	public void sendConsoleMessage(String msg) {
		// My Nice Colored Console Message Prefix.
		_cs.sendMessage(_prefix + ChatColor.GREEN + msg);
	}

	private String colorize(String s) {
		// This little code supports coloring.
		// If String is null it will return null
		if (s == null) return null;
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	private void ConfigReload() {
		getSaveItConfig().load();
		Delay = getSaveItConfig().getPluginConfig().getInt("DelayInMinutes");
		if (getSaveItConfig().Debug) sendConsoleMessage(ChatColor.GREEN + "Config Reloaded!");
	}

	/**
	 * Methods
	 *
	 */

	public static Main getInstance() {
		return instance;
	}

	public SaveItConfig getSaveItConfig() {
		return this.SaveItConfig;
	}

	public BackUp getSaveItBackup() {
		return this.backup;
	}

	public SaveItTaskManager getSaveItManager() {
		return manager;
	}

}
