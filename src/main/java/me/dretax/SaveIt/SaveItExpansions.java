package me.dretax.SaveIt;

import org.bukkit.Bukkit;
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
	private int places = 0;
	private int breaks = 0;
	private int logins = 0;
	private int quits = 0;
	private Main p = Main.getInstance();
	private SaveItConfig SaveItConfig = p.getSaveItConfig();

	@EventHandler
	private void onPlayerLoginEvent(PlayerLoginEvent e) {
		if (SaveItConfig.SaveOnLogin) {
			this.logins += 1;
			if (this.logins == (SaveItConfig.SaveOnLoginCount)) {
				p.WorldSaveDelayed();
				this.logins -= (SaveItConfig.SaveOnLoginCount);
				if (SaveItConfig.Debug) p.sendConsoleMessage(ChatColor.GREEN + "Login limit reached, reset!");
			}
		}
	}

	@EventHandler
	private void onPlayerQuitEvent(PlayerQuitEvent e) {
		if (SaveItConfig.SaveOnQuit) {
			this.quits += 1;
			if (this.quits == (SaveItConfig.SaveOnQuitCount)) {
				p.WorldSaveDelayed();
				this.quits -= (SaveItConfig.SaveOnQuitCount);
				if (SaveItConfig.Debug) p.sendConsoleMessage(ChatColor.GREEN + "Quit limit reached, reset!");
			}
		}
	}

	@EventHandler
	private void onBlockPlace(BlockPlaceEvent event) {
		if (SaveItConfig.SaveOnBlockPlace) {
			this.places += 1;
			if (this.places == (SaveItConfig.SaveOnBlockPlacecount)) {
				p.WorldSaveDelayed();
				this.places -= (SaveItConfig.SaveOnBlockPlacecount);
				if (SaveItConfig.Debug) p.sendConsoleMessage(ChatColor.GREEN + "Place limit reached, reset!");
			}
		}
	}

	@EventHandler
	private void onBlockBreak(BlockBreakEvent event) {
		if (SaveItConfig.SaveOnBlockBreak) {
			this.breaks += 1;
			if (this.breaks == (SaveItConfig.SaveOnBlockBreakcount)) {
				p.WorldSaveDelayed();
				this.breaks -= (SaveItConfig.SaveOnBlockBreakcount);
				if (SaveItConfig.Debug) p.sendConsoleMessage(ChatColor.GREEN + "Break limit reached, reset!");
			}
		}
	}
}
