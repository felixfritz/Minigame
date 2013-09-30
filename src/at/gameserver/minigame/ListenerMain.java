package at.gameserver.minigame;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerMain implements Listener {
	
	protected ListenerMain() {}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent evt) {
		if(Main.isPlayerActive(evt.getPlayer())) {
			Main.removePlayer(evt.getPlayer());
		}
	}
	
}
