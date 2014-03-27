package com.king.drunk.model;

import com.king.drunk.cast.CastCommand;
import com.king.drunk.cast.CastCommandType;
import com.king.drunk.media.router.GameCastWrapper;
import com.king.drunk.model.entity.Player;

/**
 * Created by king on 2/18/14.
 */
public class HostGameManager implements GameManager {

	private Game game;
	private GameCastWrapper gameCast;

	public HostGameManager(GameCastWrapper gameCast) {
		this.gameCast = gameCast;
		game = new Game();
	}

	@Override
	public void joinGame() {
		//Do nothing
	}

	@Override
	public void hostGame() {
		//Do nothing
	}

	@Override
	public void registerPlayer(Player player) {
		//Register player to this game.
		game.addPlayer(player);
		CastCommand castCommand = new CastCommand();
		castCommand.playerCommand(CastCommandType.addPlayer, player);
		gameCast.sendMessage(castCommand);
	}

	@Override
	public void syncGame() {

	}
}
