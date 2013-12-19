package me.dretax.SaveIt.tasks;

import me.dretax.SaveIt.Main;

public class StartDailyBackup implements Runnable {

	@Override
	public void run() {
		Main.getInstance().getSaveItBackup().kcheck();
	}
}
