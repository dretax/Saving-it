package me.dretax.SaveIt;

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
	private int places = 0;
    private int breaks = 0;
    private int logins = 0;
    private int quits = 0;
	protected Main plugin;

	
	public SaveItExpansions(Main instance)
	{
		this.plugin = instance;
	}

    @EventHandler(priority = EventPriority.LOW)
	private void onPlayerLoginEvent(PlayerLoginEvent e) {
		if (SaveItConfig.SaveOnLogin) {
			this.logins += 1;
			if (this.logins == (SaveItConfig.SaveOnLoginCount)) {
				plugin.WorldSaveDelayed();
				this.logins -= (SaveItConfig.SaveOnLoginCount);
				if (SaveItConfig.Debug) {
					sendConsoleMessage(ChatColor.GREEN + "Login limit reached, reseted!");
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
    private void onPlayerQuitEvent(PlayerQuitEvent e) {
		if (SaveItConfig.SaveOnQuit) {
			this.quits += 1;
			if (this.quits == (SaveItConfig.SaveOnQuitCount)) {
				plugin.WorldSaveDelayed();
				this.quits -= (SaveItConfig.SaveOnQuitCount);
				if (SaveItConfig.Debug) {
					sendConsoleMessage(ChatColor.GREEN + "Quit limit reached, reseted!");
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
    private void onBlockPlace(BlockPlaceEvent event) {
		if (SaveItConfig.SaveOnBlockPlace) {
			this.places += 1;
			if (this.places == (SaveItConfig.SaveOnBlockPlacecount)) {
				plugin.WorldSaveDelayed();
				this.places -= (SaveItConfig.SaveOnBlockPlacecount);
				if (SaveItConfig.Debug) {
					sendConsoleMessage(ChatColor.GREEN + "Place limit reached, reseted!");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
    private void onBlockBreak(BlockBreakEvent event) {
		if(SaveItConfig.SaveOnBlockBreak) {
			this.breaks += 1;
			if (this.breaks == (SaveItConfig.SaveOnBlockBreakcount)) {
				plugin.WorldSaveDelayed();
				this.breaks -= (SaveItConfig.SaveOnBlockBreakcount);
				if (SaveItConfig.Debug) {
					sendConsoleMessage(ChatColor.GREEN + "Break limit reached, reseted!");
				}
			}
		}
	}

    private void sendConsoleMessage(String msg) {
		// My Nice Colored Console Message Prefix.
		plugin._cs.sendMessage(plugin._prefix + ChatColor.AQUA + msg);
	}
}
