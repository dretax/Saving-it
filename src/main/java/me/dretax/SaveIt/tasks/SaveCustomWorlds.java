package me.dretax.SaveIt.tasks;

import me.dretax.SaveIt.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SaveCustomWorlds implements Runnable {

	Main p = Main.getInstance();
	private boolean b = false;

	@Override
	public void run() {
		// Checking if an Existing World is written in the Config
		for (String worldname : p.getSaveItConfig().ExWorlds) {
			if (p.getServer().getWorld(worldname) == null) {
				p.sendConsoleMessage(ChatColor.RED + "[ERROR] Not Existing World in Config!");
				p.sendConsoleMessage(ChatColor.RED + "[ERROR] " + ChatColor.BLUE + worldname + ChatColor.RED + " does not exist! Remove it from the config!");
				if (p.getSaveItConfig().BroadCastErrorIg) {
					p.getServer().broadcastMessage(p._prefix + ChatColor.RED + "[ERROR] Not Existing World In Config!");
					p.getServer().broadcastMessage(p._prefix + ChatColor.RED + "[ERROR] " + ChatColor.BLUE + worldname + ChatColor.RED + " does not exist! Remove it from the config!");
				}
				return;
			}
			p.getSaveItManager().Delay2 += 1;
			p.getServer().getWorld(worldname).save();
			// Getting All The Players, and Saving Them, only in the Configured Worlds.
			if (!p.getSaveItConfig().SavePlayersFully) {
				b = true;
				for (Player player : p.getServer().getWorld(worldname).getPlayers()) {
					player.saveData();
				}
			}
		}
		if (p.getSaveItConfig().SavingStats) {
			p.sendConsoleMessage("Took: " + String.valueOf((System.currentTimeMillis() - p.savingcheck) / 1000) + " seconds to Save World(s)");
			if (b) p.sendConsoleMessage("Including Players"); else p.sendConsoleMessage("Without Players");
		}
	}
}
