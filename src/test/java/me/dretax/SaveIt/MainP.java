package me.dretax.SaveIt;


import me.dretax.SaveIt.metrics.MetricsP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainP extends JavaPlugin {
	/*
	 *
	 * @Author: DreTaX
	 *
	 */

	FileConfiguration config;
	private List<String> ExWorlds = Arrays.asList(new String[]{"world"});
	private int Delay;
	private long Delay2;
	boolean EMSG, PWS, DDS, SOD;
	private ConsoleCommandSender _cs;
	String _prefix = ChatColor.AQUA + "[SaveIt] ", MSG, MSG2;

	public void onEnable() {
		config = this.getConfig();
		config.addDefault("Delay", 10);
		config.addDefault("Worlds", ExWorlds);
		config.addDefault("EnableMSG", true);
		config.addDefault("MSG", "&4Saving Worlds....");
		config.addDefault("MSG2", "&4Save Complete!");
		config.addDefault("PowerSave", false);
		config.addDefault("DisableDefaultWorldSave", true);
		config.options().copyDefaults(true);
		saveConfig();
		_cs = getServer().getConsoleSender();
		ConfigReload();

		/*
		 * Metrics
		 */
		try {
			MetricsP metrics = new MetricsP(this);
			metrics.start();
		}
		// Couldn't Connect.
		catch (IOException localIOException) {
			sendConsoleMessage(ChatColor.RED + "SaveIt Metrics Failed to boot! Notify DreTaX!");
		}

		if (DDS) {
			for (World world : Bukkit.getWorlds()) {
				world.setAutoSave(false);
			}
		}

		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				WSave();
			}
		}
		, 1200L * Delay, 1200L * Delay);

		sendConsoleMessage("Enabled!");

	}

	public void onDisable() {
		if (SOD) {
			for (World w : Bukkit.getWorlds()){
				w.save();
				Bukkit.savePlayers();
			}
		}
	}

	private void WSave() {
		if (PWS) {
			int players = this.getServer().getOnlinePlayers().length;
			if (players == 0) {
				return;
			}
		}

		if (EMSG) Bukkit.getServer().broadcastMessage(colorize(config.getString("MSG")));
		Delay2 = (long) 0.5;
		for (final String worldname : ExWorlds) {
			if (Bukkit.getWorld(worldname) != null) {
				Bukkit.getScheduler().runTaskLater(this, new Runnable() {
					public void run() {
						Delay2 += 0.5;
						Bukkit.getWorld(worldname).save();
						// Getting All The Players, and Saving Them, only in the Configured Worlds.
						for (Player player : Bukkit.getWorld(worldname).getPlayers()) {
							player.saveData();
						}
					}
				}
				, 20L * Delay2);
			} else {
				sendConsoleMessage(ChatColor.RED + "[ERROR] Not Existing World in Config!");
				sendConsoleMessage(ChatColor.RED + "[ERROR] " + ChatColor.BLUE + worldname + ChatColor.RED + " does not exist! Remove it from the config!");
			}
		}

		if (EMSG) Bukkit.getServer().broadcastMessage(colorize(config.getString("MSG2")));

	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("save")) {
				if (sender.hasPermission("saveit.save")) {
					WSave();
				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("saveit.reload")) {
					ConfigReload();
				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			if (args[0].equalsIgnoreCase("add")) {
				if (sender.hasPermission("saveit.manage")) {
					if (args.length == 2) {
						config = getConfig();
						ConfigReload();
						if (!ExWorlds.contains(args[1])) {
							ExWorlds.add(args[1]);
							config.set("Worlds", ExWorlds);
							saveConfig();
							ConfigReload();
							sender.sendMessage(_prefix + ChatColor.GREEN + "Added World: " + args[1]);
						} else {
							sender.sendMessage(_prefix + ChatColor.RED + "World Already Exists in config: " + args[1]);
						}
					} else sender.sendMessage(_prefix + ChatColor.RED + "Specify a World Name!");
				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			if (args[0].equalsIgnoreCase("remove")) {
				if (sender.hasPermission("saveit.manage")) {
					if (args.length == 2) {
						config = getConfig();
						ConfigReload();
						if (ExWorlds.contains(args[1])) {
							ExWorlds.remove(args[1]);
							config.set("Worlds", ExWorlds);
							saveConfig();
							ConfigReload();
							sender.sendMessage(_prefix + ChatColor.GREEN + "Removed World: " + args[1]);
						} else {
							sender.sendMessage(_prefix + ChatColor.RED + "World Doesn't Exist in config: " + args[1]);
						}
					} else sender.sendMessage(_prefix + ChatColor.RED + "Specify a World Name!");
				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
			if (args[0].equalsIgnoreCase("list")) {
				if (sender.hasPermission("saveit.manage")) {
					config = getConfig();
					ConfigReload();
					sender.sendMessage(_prefix + ChatColor.GREEN + ExWorlds);
				} else sender.sendMessage(_prefix + ChatColor.RED + "You Don't Have Permission to do this!");
			}
		}
		else {
			sender.sendMessage(_prefix + ChatColor.GREEN + "Performance Version 1.0 " + ChatColor.AQUA + "===Commands:===");
			sender.sendMessage(ChatColor.BLUE + "/saveit save" + ChatColor.GREEN + " - Saves All the Configured Worlds, and Inventories" + ChatColor.YELLOW + "(FULLSAVE)");
			sender.sendMessage(ChatColor.BLUE + "/saveit reload" + ChatColor.GREEN + " - Reloads Config");
			sender.sendMessage(ChatColor.BLUE + "/saveit add " + ChatColor.YELLOW + "WORLDNAME (Case Sensitive)" + ChatColor.GREEN + " - Adds a Given World to Config");
			sender.sendMessage(ChatColor.BLUE + "/saveit remove " + ChatColor.YELLOW + "WORLDNAME (Case Sensitive)" + ChatColor.GREEN + " - Removes a Given World from Config");
			sender.sendMessage(ChatColor.BLUE + "/saveit list" + ChatColor.GREEN + " - Lists Current Worlds in Config");
		}
		return false;
	}

	private void ConfigReload() {
		Delay = config.getInt("Delay");
		ExWorlds = config.getStringList("Worlds");
		EMSG = config.getBoolean("EnableMSG");
		MSG = config.getString("MSG");
		MSG2 = config.getString("MSG2");
		PWS = config.getBoolean("PowerSave");
		DDS = config.getBoolean("DisableDefaultWorldSave");

	}

	private void sendConsoleMessage(String msg) {
		// My Nice Colored Console Message Prefix.
		_cs.sendMessage(_prefix + ChatColor.GREEN + msg);
	}

	private String colorize(String s) {
		// This little code supports coloring.
		// If String is null it will return null
		if (s == null) return null;
		// Extra Stuff, taken from My SimpleNames Plugin
		s = s.replaceAll("&r", ChatColor.RESET + "");
		s = s.replaceAll("&l", ChatColor.BOLD + "");
		s = s.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
		s = s.replaceAll("&o", ChatColor.ITALIC + "");
		s = s.replaceAll("&n", ChatColor.UNDERLINE + "");
		//This one Supports all the Default Colors
		return s.replaceAll("&([0-9a-f])", "\u00A7$1");
	}
}
