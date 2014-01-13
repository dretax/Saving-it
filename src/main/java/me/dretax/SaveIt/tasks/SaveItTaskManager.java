package me.dretax.SaveIt.tasks;

import me.dretax.SaveIt.Main;
import me.dretax.SaveIt.SaveItConfig;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Date;

public class SaveItTaskManager {

	private BukkitScheduler _bukkitScheduler;
	private Main p = Main.getInstance();
	private SaveItConfig config = p.getSaveItConfig();
	public int Delay2 = 1;

	public SaveItTaskManager() {
		_bukkitScheduler = Bukkit.getScheduler();
	}

	// Backup Times
	private long hour = (long) 1200L * 60;

	public void StartAutoSave() {
		_bukkitScheduler.runTaskTimer(p, new StartAutoSave(), 1200L * p.Delay, 1200L * p.Delay);
	}

	public void StartPurge() {
		_bukkitScheduler.runTaskTimer(p, new PurgeBackups(), hour, hour);
	}

	public void StartIntervalBackup() {
		_bukkitScheduler.runTaskTimer(p, new StartIntervalBackup(), 72000L * p.getSaveItConfig().intv, 72000L * p.getSaveItConfig().intv);
	}

	public void StartDailyBackup() {
		_bukkitScheduler.runTaskTimer(p, new StartDailyBackup(), hour, hour);
	}

	public void StartSmoothingOnAllWorlds() {
		Delay2 = 1;
		_bukkitScheduler.runTaskLater(p, new SaveAllWorlds(), 20L * Delay2);
	}

	public void StartSmoothingonCustomWorlds() {
		Delay2 = 1;
		_bukkitScheduler.runTaskLater(p, new SaveCustomWorlds(), 20L * Delay2);
	}

	public void StartSavingAllPlayers() {
		Delay2 = 1;
		_bukkitScheduler.runTaskLater(p, new SaveAllPlayers(), 20L * Delay2);
	}

	public void StartManualBackup() {
		_bukkitScheduler.runTaskLater(p, new StartManualBackup(), 20L * 5);
	}

	@SuppressWarnings("unused")
	private long s(double s) {
		double n = h(new Date());
		double d = n - config.StartOnAGivenHour;
		if (d < 0) {
			d += 24;
		}
		double ip = d - Math.floor(d / config.intv) * config.intv;
		double r = config.intv - ip;
		return (long) (r * 72000);
	}

	@SuppressWarnings("deprecation")
	public static double h(Date t) {
		return t.getHours() + t.getMinutes() / 60. + t.getSeconds() / 3600.;
	}

	/*public BukkitScheduler getBukkitCheduler() {
		return _bukkitScheduler;
	}*/
}
