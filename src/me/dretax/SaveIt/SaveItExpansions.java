package me.dretax.SaveIt;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SaveItExpansions implements Listener {
	
	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent e) {
		if (Main.SaveOnLogin) {
			Main.WorldSave();
		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		if (Main.SaveOnQuit) {
			Main.WorldSave();
		}
	}
}
