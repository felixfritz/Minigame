package at.gameserver.minigame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class MiniGame {
	
	private String name;
	private int min;
	private int max;
	private boolean isRunning;
	private boolean isInCountdown;
	
	private List<Player> players;
	private Location lobbySpawn;
	
	private int PID;
	
	public MiniGame(String name) {
		this.name = name;
		this.min = 1;
		this.max = 100;
		this.players = new ArrayList<Player>();
		this.isRunning = false;
		this.isInCountdown = false;
		this.lobbySpawn = null;
	}
	
	
	/**
	 * Get the name of this minigame.
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	
	
	/**
	 * Get the least amount of people that have to join the arena in order for the countdown to start.<br>
	 * Default is 1.
	 * @return min amount
	 */
	public int getMinAmount() {
		return this.min;
	}
	
	
	/**
	 * Set the number for the least amount of people that have to join the arena in order for the countdown to start.<br>
	 * If the min amount is higher than the max amount, the numbers will be switched.<br>
	 * Default is 1. May not be below 1!
	 * @param min amount
	 */
	public void setMinAmount(int min) {
		if(min > max) {
			int tmp = this.min;
			this.min = max;
			max = tmp;
		} else if(min > 0) {
			this.min = min;
		}
	}
	
	
	/**
	 * Get the full amount of people that can join the arena.<br>
	 * Default is 100.
	 * @return max amount
	 */
	public int getMaxAmount() {
		return this.max;
	}
	
	
	/**
	 * Set the maximum amount of people that can join the arena.<br>
	 * If the max amount is lower than the min amount, the numbers will be switched.<br>
	 * Default is 100. May not be below 1!
	 * @param max amount
	 */
	public void setMaxAmount(int max) {
		if(max < min) {
			int tmp = this.max;
			this.max = min;
			min = tmp;
		} else if(max > 0) {
			this.max = max;
		}
	}
	
	
	/**
	 * Check, if people can't join anymore.
	 * @return true, if the lobby is full and no one can join.
	 */
	public boolean isFull() {
		return this.max == this.players.size();
	}
	
	
	/**
	 * Look at how many people are in the minigame.
	 * @return number of players
	 */
	public int getAmountOfPlayers() {
		return this.players.size();
	}
	
	
	/**
	 * Get the location, where players spawn when joining a lobby
	 * @return lobbySpawn
	 */
	public Location getLobbySpawn() {
		return this.lobbySpawn;
	}
	
	
	/**
	 * Set the spawn location for the lobby.
	 * @param location
	 */
	public void setLobbySpawn(Location location) {
		this.lobbySpawn = location;
	}
	
	
	/**
	 * Check, if the minigame has already started or not.
	 * @return true, if it has started
	 */
	public boolean isRunning() {
		return this.isRunning;
	}
	
	
	/**
	 * Check, if the minigame has already started or not.
	 * @return true, if it has started
	 */
	public boolean hasStarted() {
		return this.isRunning;
	}
	
	
	/**
	 * Check, if the players in the lobby are already in the countdown.
	 * @return true, if that's the case. Return false, if the game has started or nothing happened yet.
	 */
	public boolean isInCountdown() {
		return this.isInCountdown;
	}
	
	
	/**
	 * Add a player to the minigame
	 * @param p
	 * @return true, if the player successfully joined.
	 */
	public boolean addPlayerToMinigame(Player player) {
		if(players.size() + 1 > max)
			return false;
		if(!Main.isPlayerActive(player)) {
			player.setExp(0);
			player.setLevel(0);
			if(players.size() + 1 >= min)
				startCountdown();
			
			sendMessageToPlayers(Main.getMessage(Message.PLAYER_JOIN_PUBLIC));
			player.sendMessage(Main.getMessage(Message.PLAYER_JOIN_SINGLE));
			if(lobbySpawn != null)
				player.teleport(lobbySpawn);
			return players.add(player);
		}
		return false;
	}
	
	
	/**
	 * Remove player from the minigame.
	 * @param player that has to be removed
	 * @return false, if it wasn't successful
	 */
	public boolean removePlayerFromMinigame(Player player) {
		if(!hasPlayer(player))
			return false;
		
		for(int x = 0; x < players.size(); x++) {
			if(players.get(x).getName().equals(player.getName())) {
				if(isInCountdown && players.size() - 1 < min)
					cancelCountdown();
				
				if(players.remove(x) != null) {
					sendMessageToPlayers(Main.getMessage(Message.PLAYER_LEAVE_PUBLIC));
					player.sendMessage(Main.getMessage(Message.PLAYER_LEAVE_SINGLE));
					return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * Check and see, if that minigame has that player
	 * @param player
	 * @return true, if he's in here
	 */
	public boolean hasPlayer(Player player) {
		return hasPlayer(player.getName());
	}
	
	
	/**
	 * Check and see, if that minigame has that player (-name)
	 * @param playername
	 * @return true, if he's in here
	 */
	public boolean hasPlayer(String name) {
		for(Player p : players) {
			if(p.getName().equals(name))
				return true;
		}
		return false;
	}
	
	
	/**
	 * Get the whole list of players in the game.
	 * @return players
	 */
	public List<Player> getPlayers() {
		return this.players;
	}
	
	
	/**
	 * Send a message to all players in the minigame. It replaces "game" with the name of the game, "max" with the max amount of players that can join,
	 * "min" with the least amount of people that have to join and "amount" with the current amount of people in the lobby.
	 * @param message
	 */
	public void sendMessageToPlayers(String message) {
		message = message.replaceAll("<game>", message).replaceAll("<max>", String.valueOf(max)).replaceAll("<min>", String.valueOf(min))
				.replaceAll("<amount>", String.valueOf(players.size()));
		
		for(Player p : players)
			p.sendMessage(message);
	}
	
	
	/**
	 * End the minigame. Sets the isRunning boolean to false, sends GAME_FINISH message to players and removes
	 * all players from the list.
	 */
	public void finish() {
		isRunning = false;
		isInCountdown = false;
		sendMessageToPlayers(Main.getMessage(Message.GAME_FINISH));
		this.players.clear();
	}
	
	
	/**
	 * Start the countdown anyway, no matter how many people are in there
	 */
	public void forceStartCountdown() {
		startCountdown();
	}
	
	
	/**
	 * Teleport all players to a specific location
	 * @param location
	 */
	public void teleportPlayers(Location location) {
		for(Player p : players)
			p.teleport(location);
	}
	
	
	private void startCountdown() {
		sendMessageToPlayers(Main.getMessage(Message.COUNTDOWN_START));
		isInCountdown = true;
		
		PID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
			int time = 10;
			
			public void run() {
				for(Player p : players)
					p.setLevel(time--);
			}
			
		}, 0, 10 * 20);
				
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
			
			public void run() {
				isInCountdown = false;
				if(Bukkit.getScheduler().isCurrentlyRunning(PID))
					Bukkit.getScheduler().cancelTask(PID);
				
				if(isReadyToStart())
					start();
			}
			
		}, 10 * 20);
	}
	
	
	private void cancelCountdown() {
		isInCountdown = false;
		Bukkit.getScheduler().cancelTask(PID);
		sendMessageToPlayers(Main.getMessage(Message.COUNTDOWN_CANCEL));
		for(Player p : players)
			p.setLevel(0);
	}
	
	private void start() {
		isRunning = true;
		sendMessageToPlayers(Main.getMessage(Message.GAME_START));
		startGame();
	}
	
	public abstract boolean isReadyToStart();

	public abstract void startGame();
}
