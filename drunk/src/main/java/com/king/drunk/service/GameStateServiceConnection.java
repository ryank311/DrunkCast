package com.king.drunk.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by king on 2/18/14.
 */
public class GameStateServiceConnection implements ServiceConnection {

	private GameStateService gameStateService;
	private boolean connected = false;

	@Override
	public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
		GameStateService.GameBinder binder = (GameStateService.GameBinder) iBinder;
		gameStateService = binder.getService();
		connected = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName componentName) {
		connected = false;
	}

	public boolean isConnected() {
		return connected;
	}

	public GameStateService getGameStateService() {
		return gameStateService;
	}
}
