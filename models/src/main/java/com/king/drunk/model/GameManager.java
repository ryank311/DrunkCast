package com.king.drunk.model;

import com.king.drunk.model.entity.Player;

/**
 * Created by king on 2/18/14.
 */
public interface GameManager {
	void joinGame();

	void hostGame();

	void registerPlayer(Player player);

	void syncGame();
}
