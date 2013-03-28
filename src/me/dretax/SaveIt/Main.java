package me.dretax.SaveIt;

import java.io.IOException;
import me.dretax.SaveIt.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
	public PluginManager _pm;
	public static ConsoleCommandSender _cs;
	public static final String _prefix = ChatColor.AQUA
			+ "[SaveIt] ";
	public static boolean EnableMsg;
	
	public void onDisable()
	{
		super.onDisable();
	}
	
  
	public void onEnable() {
		this._pm = getServer().getPluginManager();
		_cs = getServer().getConsoleSender();
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
			sendConsoleMessage(ChatColor.GREEN + "SaveIt Metrics Successfully Enabled!");
		}
		catch (IOException localIOException) {
		}
		getCommand("saveit").setExecutor(this);
		getConfig().addDefault("DelayInMinutes", Integer.valueOf(10));
		getConfig().addDefault("World1", "world");
		getConfig().addDefault("World2", "world_nether");
		getConfig().addDefault("EnableSaveMSG", true);
		getConfig().addDefault("SaveMSG", "&aStarting world save...");
		getConfig().addDefault("SaveMSG2", "&aWorld save completed!");
		getConfig().options().copyDefaults(true);
		saveConfig();
		EnableMsg = getConfig().getBoolean("EnableSaveMSG");
		int delay = getConfig().getInt("DelayInMinutes");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			public void run() {
				WorldSave();
			}
		}
		, 1200L * delay, 1200L * delay);
		sendConsoleMessage(ChatColor.GREEN + "SaveIt Successfully Enabled!");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    if (!sender.hasPermission("saveit.save")) {
	        return false;
	      }
	    WorldSave();
		return true;
	}
  
	public void WorldSave(){
		if (EnableMsg) {
			Bukkit.getServer().broadcastMessage(colorize(Main.this.getConfig().getString("SaveMSG")));
		}
		boolean saving=true;
		for (World world : Bukkit.getServer().getWorlds()) {
			int World=0;
			while(saving){
				World++;
	    		if(Main.this.getConfig().getString("World"+World).equals(world.getName())){
	    	        world.save();
	    	        for (Player player : world.getPlayers()) {
	    	        	player.saveData();
	    	        }
	    	        saving=false;
	    		}
	    		if(Main.this.getConfig().getString("World"+World).length()<1)saving=false;
			}
		}
	    if (EnableMsg) {
	    	Bukkit.getServer().broadcastMessage(colorize(Main.this.getConfig().getString("SaveMSG2")));
	    }
	}
	
	public static void sendConsoleMessage(String msg) {
		_cs.sendMessage(_prefix + ChatColor.AQUA + msg);
	}
	
	public static String colorize(String s){
	    if(s == null) return null;
	    return s.replaceAll("&([0-9a-f])", "\u00A7$1");
	}
	
}