package at.gameserver.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private static List<MiniGame> games;
	private static Map<Message, String> messages;
	
	private static Main plugin;
	
	public void onEnable() {
		
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		
		games = new ArrayList<MiniGame>();
		plugin = this;
		getServer().getPluginManager().registerEvents(new ListenerMain(), this);
		
		messages = new HashMap<Message, String>();
		
		for(Message msg : Message.values())
			messages.put(msg, ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages." + msg.toString().toLowerCase())));
		
	}
	
	public void onDisable() {
		games = null;
		plugin = null;
		messages = null;
	}
	
	
	/**
	 * Check, if player is in one of the minigames.
	 * @param name
	 * @return true, if he's active
	 */
	public static boolean isPlayerActive(String name) {
		for(MiniGame game : games) {
			if(game.hasPlayer(name))
				return true;
		}
		return false;
	}
	
	
	/**
	 * Check, if player is in one of the minigames.
	 * @param name
	 * @return true, if he's active
	 */
	public static boolean isPlayerActive(Player p) {
		return isPlayerActive(p.getName());
	}
	
	
	/**
	 * 
	 * @param miniGame
	 * @return
	 */
	public static boolean addMiniGame(MiniGame miniGame) {
		if(hasMiniGame(miniGame.getName()))
			return false;
		return games.add(miniGame);
	}
	
	
	/**
	 * Get a minigame.
	 * @param name of the game
	 * @return game of the name
	 */
	public static MiniGame getMiniGame(String name) {
		for(MiniGame game : games) {
			if(game.getName().equalsIgnoreCase(name))
				return game;
		}
		return null;
	}
	
	
	public static boolean removePlayer(Player player) {
		for(MiniGame game : games) {
			if(game.removePlayerFromMinigame(player))
				return true;
		}
		return false;
	}
	
	
	/**
	 * Check, if the list of minigames has that one game.
	 * @param name
	 * @return true, if that minigame exists
	 */
	public static boolean hasMiniGame(String name) {
		return getMiniGame(name) != null;
	}
	
	
	/**
	 * Get all the games available.
	 * @return games
	 */
	public static List<MiniGame> getMiniGames() {
		return games;
	}
	
	
	/**
	 * Get plugin.
	 * @return plugin
	 */
	protected static Main getPlugin() {
		return plugin;
	}
	
	
	/**
	 * Get message.
	 * @param msg
	 * @return string message
	 */
	public static String getMessage(Message msg) {
		return messages.get(msg);
	}
	
	
	/**
	 * Set message.
	 * @param msg
	 * @param sentence
	 */
	public static void setMessage(Message msg, String sentence) {
		messages.remove(msg);
		messages.put(msg, sentence);
	}
}
