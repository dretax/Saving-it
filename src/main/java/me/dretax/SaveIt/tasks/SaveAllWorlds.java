package me.dretax.SaveIt.tasks;

import me.dretax.SaveIt.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SaveAllWorlds implements Runnable {

	Main p = Main.getInstance();

	@Override
	public void run() {
		for (World world : Bukkit.getWorlds()) {
			p.getSaveItManager().Delay2 += 1;
			world.save();
			// Getting All The Players, and Saving Them, only in the Configured Worlds.
			if (!p.getSaveItConfig().SavePlayersFully) {
				for (Player player : world.getPlayers()) {
					player.saveData();
				}
			}
		}
	}
}
