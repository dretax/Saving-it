package me.dretax.SaveIt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;

public class SaveItUpdate {
	/*
	 * @Author: DreTaX | SaveIt Update Checker File, rewritten from scratch.
	 */

	private Main plugin;
	String updateVersion;
	int curVer;
	int updateVer;

	protected SaveItUpdate(Main instance) {
		this.plugin = instance;
	}

	protected Boolean isLatest() {
		sendConsoleMessage(ChatColor.GREEN + "Checking for updates. Please wait.");
		try {
			updateVer = 0;
			curVer = 0;
			URLConnection yc = new URL("https://raw.github.com/dretax/Saving-it/master/update.txt").openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			PluginDescriptionFile pdf = this.plugin.getDescription();
			String version = pdf.getVersion();
			this.updateVersion = in.readLine().replace(".", "");
			updateVer = Integer.parseInt(this.updateVersion);
			curVer = Integer.parseInt(version.replace(".", ""));
			if (updateVer > curVer) {
				sendConsoleMessage(ChatColor.RED + "A new version of SaveIt is available:  " + ChatColor.GREEN + this.updateVersion);
				sendConsoleMessage(ChatColor.RED + "Your current version is:  " + ChatColor.GREEN + version);
				sendConsoleMessage(ChatColor.RED + "Get it From: http://dev.bukkit.org/server-mods/automatically-world-saving/");
				return false;
			}
			sendConsoleMessage(ChatColor.GREEN + "No Updates Found...You are cool! :D");
			in.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			sendConsoleMessage(ChatColor.GREEN + "Error Occured while check, notify DreTaX!");
		}
		return true;
	}

	protected String getUpdateVersion() {
		return this.updateVersion;
	}

	private void sendConsoleMessage(String msg) {
		plugin._cs.sendMessage(plugin._prefix + ChatColor.AQUA + msg);
	}

}