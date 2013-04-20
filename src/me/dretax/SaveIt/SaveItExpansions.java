package me.dretax.SaveIt;

import java.util.logging.Logger;


import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SaveItExpansions implements Listener {
	
	/*
	 * @Author: DreTaX
	 */
	protected final Logger logger = Logger.getLogger("Minecraft");
	protected static Main plugin;

	public SaveItExpansions(Main instance)
	{
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent e) {
		if (Main.SaveOnLogin) {
			SaveItAccessor.logins += 1;
			if (SaveItAccessor.logins == (Main.SaveOnLoginCount)) {
				Main.WorldSave();
				SaveItAccessor.logins -= (Main.SaveOnLoginCount);
				Main.sendConsoleMessage(ChatColor.GREEN + "Login number reached, reseted!");
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		if (Main.SaveOnQuit) {
			SaveItAccessor.quits += 1;
			if (SaveItAccessor.quits == (Main.SaveOnQuitCount)) {
				Main.WorldSave();
				SaveItAccessor.quits -= (Main.SaveOnQuitCount);
				Main.sendConsoleMessage(ChatColor.GREEN + "Quit number reached, reseted!");
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (Main.SaveOnBlockBreak) {
			SaveItAccessor.places += 1;
			if (SaveItAccessor.places == (Main.SaveOnBlockPlacecount)) {
				Main.WorldSave();
				SaveItAccessor.places -= (Main.SaveOnBlockPlacecount);
				Main.sendConsoleMessage(ChatColor.GREEN + "BlockPlace number reached, reseted!");
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(Main.SaveOnBlockPlace) {
			SaveItAccessor.breaks += 1;
			if (SaveItAccessor.breaks == (Main.SaveOnBlockBreakcount)) {
				Main.WorldSave();
				SaveItAccessor.breaks -= (Main.SaveOnBlockBreakcount);
				Main.sendConsoleMessage(ChatColor.GREEN + "BlockBreak number reached, reseted!");
			}
		}
	}
}
