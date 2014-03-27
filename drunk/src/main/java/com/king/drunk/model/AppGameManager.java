package com.king.drunk.model;

import android.content.Context;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.CastMediaControlIntent;
import com.king.drunk.R;
import com.king.drunk.lib.GameClient;
import com.king.drunk.lib.GameServer;
import com.king.drunk.lib.client.CallbackType;
import com.king.drunk.lib.client.ClientEventCallback;
import com.king.drunk.media.router.GameCastWrapper;
import com.king.drunk.media.router.MediaRouterCallback;
import com.king.drunk.model.entity.Player;

/**
 * Created by king on 2/18/14.
 */
public class AppGameManager implements GameManager {

	protected Game game;
	protected GameServer gameServer;
	protected GameClient gameClient;
	protected GameCastWrapper gameCast;

	private static AppGameManager appGameManager;

	private AppGameManager() {
		game = new Game();
		gameClient = new GameClient();
	}

	/**
	 * Get the instance of this manager
	 * @return
	 */
	public static synchronized AppGameManager getInstance() {
		if(appGameManager != null) return appGameManager;
		appGameManager = new AppGameManager();
		return appGameManager;
	}

	/**
	 * Method to setup the channel for casting.
	 * @param callback
	 * @param appContext
	 */
	public void setupCast(
			MediaRouterCallback callback,
			Context appContext) {
		gameCast = new GameCastWrapper(
				MediaRouter.getInstance(appContext),
				callback,
				new MediaRouteSelector.Builder().addControlCategory(
						CastMediaControlIntent.categoryForCast(appContext.getString(R.string.app_id))).build());
	}

	/**
	 * Helper to return the instance of the gamecast
	 * @return
	 */
	public GameCastWrapper getGameCast() {
		return gameCast;
	}

	/**
	 * Register the callback for game connection/failure event
	 * @param callback
	 */
	public void registerGameConnectedCallback(ClientEventCallback callback) {
		gameClient.registerCallback(CallbackType.CONNECTION, callback);
	}

	/**
	 * Register the new player callback.
	 * @param callback
	 */
	public void registerPlayerRegistrationCallback(ClientEventCallback callback) {
		gameClient.registerCallback(CallbackType.PLAYER_CREATION, callback);
	}


	/**
	 * Join an existing game on the network
	 */
	@Override
	public void joinGame() {
		gameClient.searchForGameServers();
	}

	/**
	 * Spawn a new host for the game
	 */
	@Override
	public void hostGame() {
		if(gameServer == null) {
			gameServer = new GameServer(gameCast);
			new Thread(gameServer).start();
		} else {
			gameServer.resetServer(gameCast);
		}
	}

	/**
	 * Helper method to register a new player with the game.
	 * @param name
	 * @param color
	 */
	public void registerPlayer(String name, int color) {
		Player newPlayer = new Player(name, gameClient.getClientId(), color);
		registerPlayer(newPlayer);
	}

	@Override
	public void registerPlayer(Player player) {
		gameClient.registerPlayer(player);
	}

	/**
	 * Reconcile game state with the host
	 */
	@Override
	public void syncGame() {

	}

}
