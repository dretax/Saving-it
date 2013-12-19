package me.dretax.SaveIt.tasks;

import me.dretax.SaveIt.Main;

public class StartAutoSave implements Runnable {

	@Override
	public void run() {
		Main.getInstance().WorldSaveDelayed();
	}
}
