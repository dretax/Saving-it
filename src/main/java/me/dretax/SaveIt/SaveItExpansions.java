package me.dretax.SaveIt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;

public class SaveItExpansions implements Listener {

	/*
	 * @Author: DreTaX
	 */
	private int places = 0;
	private int breaks = 0;
	private int logins = 0;
	private int quits = 0;
	Main p;
	SaveItConfig SaveItConfig;


	protected SaveItExpansions(Main i, SaveItConfig i2) {
		this.p = i;
		this.SaveItConfig = i2;
	}

	@EventHandler
	private void onPlayerLoginEvent(PlayerLoginEvent e) {
		if (SaveItConfig.SaveOnLogin) {
			this.logins += 1;
			if (this.logins == (SaveItConfig.SaveOnLoginCount)) {
				gP().WorldSaveDelayed();
				this.logins -= (SaveItConfig.SaveOnLoginCount);
				if (SaveItConfig.Debug) sendConsoleMessage(ChatColor.GREEN + "Login limit reached, reset!");
			}
		}
	}

	@EventHandler
	private void onPlayerQuitEvent(PlayerQuitEvent e) {
		if (SaveItConfig.SaveOnQuit) {
			this.quits += 1;
			if (this.quits == (SaveItConfig.SaveOnQuitCount)) {
				gP().WorldSaveDelayed();
				this.quits -= (SaveItConfig.SaveOnQuitCount);
				if (SaveItConfig.Debug) sendConsoleMessage(ChatColor.GREEN + "Quit limit reached, reset!");
			}
		}
	}

	@EventHandler
	private void onBlockPlace(BlockPlaceEvent event) {
		if (SaveItConfig.SaveOnBlockPlace) {
			this.places += 1;
			if (this.places == (SaveItConfig.SaveOnBlockPlacecount)) {
				gP().WorldSaveDelayed();
				this.places -= (SaveItConfig.SaveOnBlockPlacecount);
				if (SaveItConfig.Debug) sendConsoleMessage(ChatColor.GREEN + "Place limit reached, reset!");
			}
		}
	}

	@EventHandler
	private void onBlockBreak(BlockBreakEvent event) {
		if (SaveItConfig.SaveOnBlockBreak) {
			this.breaks += 1;
			if (this.breaks == (SaveItConfig.SaveOnBlockBreakcount)) {
				gP().WorldSaveDelayed();
				this.breaks -= (SaveItConfig.SaveOnBlockBreakcount);
				if (SaveItConfig.Debug) sendConsoleMessage(ChatColor.GREEN + "Break limit reached, reset!");
			}
		}
	}

	private void sendConsoleMessage(String msg) {
		// My Nice Colored Console Message Prefix.
		Bukkit.getConsoleSender().sendMessage(gP()._prefix + ChatColor.AQUA + msg);
	}

	List<String> thosewhohadntsayhello = new ArrayList<String>();

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{
		Player p = e.getPlayer();

		if (thosewhohadntsayhello.contains(p.getName())) {
			if(!e.getMessage().contains("Hello"))
			{
				p.sendMessage("First you must type: " + ChatColor.RED + "Hello" + ChatColor.WHITE + " then you can send messages.");
				e.setCancelled(true);
			}
			else {
				thosewhohadntsayhello.remove(p.getName());
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		thosewhohadntsayhello.add(p.getName());
	}

	private Main gP() {
		return this.p;
	}
}
