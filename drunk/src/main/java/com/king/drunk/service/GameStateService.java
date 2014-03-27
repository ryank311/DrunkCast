package com.king.drunk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Transport layer for service API between clients.
 * Created by king on 2/18/14.
 */
public class GameStateService extends Service {

	//Service API




	//Necessary methods for binding to this service
	private final IBinder mBinder = new GameBinder();

	public class GameBinder extends Binder {
		GameStateService getService() {
			return GameStateService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
}
