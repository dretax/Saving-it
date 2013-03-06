package me.dretax.SaveIt;

import java.io.IOException;

import me.dretax.SaveIt.metrics.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
*
* @author DreTaX
*/

public class Main extends JavaPlugin {
	public void onDisable() {
		super.onDisable();
	}

	public void onEnable() {
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
		getCommand("saveit").setExecutor(this);
		getConfig().addDefault("DelayInMinutes", Integer.valueOf(10));
		getConfig().addDefault("Save", "Starting world save...");
		getConfig().addDefault("Save2", "World save completed!");
		getConfig().addDefault("World1", "world");
		getConfig().addDefault("World2", "world_nether");
		getConfig().addDefault("World3", "world_the_end");
		getConfig().options().copyDefaults(true);
		saveConfig();
		int delay = getConfig().getInt("DelayInMinutes");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				WorldSave();
			}
		}, 1200L * delay, 1200L * delay);
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (command.getName().equalsIgnoreCase("saveit"))
			if (sender.hasPermission("saveit.save"))
				WorldSave();
		return true;
	}

	public void WorldSave() {
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + (Main.this.getConfig().getString("Save")));
		boolean saving = true;
		for (World world : Bukkit.getServer().getWorlds()) {
			int World = 0;
			while (saving) {
				World++;
				if (this.getConfig().getString("World" + World).equals(world.getName())) {
					world.save();
					for (Player player : world.getPlayers()) {
						player.saveData();
					}
					saving = false;
				}
				if (this.getConfig().getString("World" + World).length() < 1)
					saving = false;
			}
		}
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + (Main.this.getConfig().getString("Save2")));
	}
}