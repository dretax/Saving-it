package me.dretax.SaveIt.tasks;

import me.dretax.SaveIt.Main;

public class StartManualBackup implements Runnable {

	@Override
	public void run() {
		Main.getInstance().getSaveItBackup().backupdir();
	}
}
