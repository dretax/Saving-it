package me.dretax.SaveIt;

import java.io.IOException;

import me.dretax.SaveIt.metrics.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin
{
  public void onDisable()
  {
    super.onDisable();
  }

  public void onEnable() {
	try {
	  Metrics metrics = new Metrics(this);
	  metrics.start();
	}
	catch (IOException localIOException) {
	}
    getCommand("saveit").setExecutor(this);
    getConfig().addDefault("DelayInMinutes", Integer.valueOf(10));
    getConfig().addDefault("World1", "world");
    getConfig().addDefault("World2", "world_the_end");
    getConfig().addDefault("World3", "world_nether");
    getConfig().addDefault("SaveMSG", "Starting world save...");
    getConfig().addDefault("SaveMSG2", "World save completed!");
    getConfig().options().copyDefaults(true);
    saveConfig();
    int delay = getConfig().getInt("DelayInMinutes");
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
    {
      public void run() {
    	  WorldSave();
      }
    }
    , 1200L * delay, 1200L * delay);
  }

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    if (!sender.hasPermission("saveit.save")) {
	        return false;
	      }
	    WorldSave();
		return true;
	}
  
  public void WorldSave(){
	    Bukkit.getServer().broadcastMessage(ChatColor.GREEN + Main.this.getConfig().getString("SaveMSG"));
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
	    Bukkit.getServer().broadcastMessage(ChatColor.GREEN + Main.this.getConfig().getString("SaveMSG2"));
  }
}