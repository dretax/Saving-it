package me.dretax.SaveIt.tasks;

import me.dretax.SaveIt.Main;

public class StartIntervalBackup implements Runnable {

	@Override
	public void run() {
		Main main = Main.getInstance();
		if (main.getSaveItConfig().PowerSave) {
			int players = main.getServer().getOnlinePlayers().length;
			if (players != 0) {
				main.getSaveItBackup().backupdir();
			}
		} else {
			System.out.println("sasss");
			main.getSaveItBackup().backupdir();
		}
	}
}
