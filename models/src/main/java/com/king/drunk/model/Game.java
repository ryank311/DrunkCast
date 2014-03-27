package com.king.drunk.model;

import com.king.drunk.model.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by king on 2/18/14.
 */
public class Game {
	private Map<String, Player> players;

	public Game() {
		players = new HashMap<String, Player>();
	}

	public void setPlayers(HashMap<String, Player> players) {
		this.players = players;
	}

	public void addPlayer(Player player) {
		if(player == null || player.getUuid() == null) return;
		players.put(player.getUuid(), player);
	}

	public Map<String, Player> getPlayers() {
		return players;
	}
}
