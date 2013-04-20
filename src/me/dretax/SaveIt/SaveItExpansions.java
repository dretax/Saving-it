package me.dretax.SaveIt;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SaveItExpansions implements Listener {
	
	/*
	 * @Author: DreTaX
	 */
	protected int places = 0;
	protected int breaks = 0;
	protected int logins = 0;
	protected int quits = 0;
	protected Main plugin;
	Logger log = Logger.getLogger("Minecraft");
	
	public SaveItExpansions(Main instance)
	{
		this.plugin = instance;
	}
	

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerLoginEvent(PlayerLoginEvent e) {
		if (plugin.SaveOnLogin) {
			this.logins += 1;
			if (this.logins == (plugin.SaveOnLoginCount)) {
				plugin.WorldSave();
				this.logins -= (plugin.SaveOnLoginCount);
				sendConsoleMessage(ChatColor.GREEN + "Login limit reached, reseted!");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		if (plugin.SaveOnQuit) {
			this.quits += 1;
			if (this.quits == (plugin.SaveOnQuitCount)) {
				plugin.WorldSave();
				this.quits -= (plugin.SaveOnQuitCount);
				sendConsoleMessage(ChatColor.GREEN + "Quit limit reached, reseted!");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (plugin.SaveOnBlockBreak) {
			this.places += 1;
			if (this.places == (plugin.SaveOnBlockPlacecount)) {
				plugin.WorldSave();
				this.places -= (plugin.SaveOnBlockPlacecount);
				sendConsoleMessage(ChatColor.GREEN + "Place limit reached, reseted!");
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		if(plugin.SaveOnBlockPlace) {
			this.breaks += 1;
			if (this.breaks == (plugin.SaveOnBlockBreakcount)) {
				plugin.WorldSave();
				this.breaks -= (plugin.SaveOnBlockBreakcount);
				sendConsoleMessage(ChatColor.GREEN + "Break limit reached, reseted!");
			}
		}
	}
	
	public void sendConsoleMessage(String msg) {
		// My Nice Colored Console Message Prefix.
		plugin._cs.sendMessage(plugin._prefix + ChatColor.AQUA + msg);
	}
}
