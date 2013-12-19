package me.dretax.SaveIt.tasks;

import me.dretax.SaveIt.Main;

public class SaveAllPlayers implements Runnable {

	Main p = Main.getInstance();

	@Override
	public void run() {
		p.getSaveItManager().Delay2 += 1;
		p.getServer().savePlayers();
	}
}
