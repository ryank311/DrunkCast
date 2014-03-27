package com.king.drunk.lib;

import android.util.Log;

import com.king.drunk.lib.client.CallbackType;
import com.king.drunk.lib.client.ClientEventCallback;
import com.king.drunk.lib.client.event.ClientEvent;
import com.king.drunk.lib.net.NetClientConnector;
import com.king.drunk.lib.util.IPUtils;
import com.king.drunk.model.entity.Player;
import com.king.drunk.model.net.NetCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by king on 2/19/14.
 */
public class GameClient {
	private static final String TAG = GameClient.class.getSimpleName();

	private boolean connected = false;
	private int port = 39249;
	private String hostAddress;
	private String clientId;
	private Map<CallbackType, ClientEventCallback> callbackMap;

	public GameClient() {
		callbackMap = new HashMap<CallbackType, ClientEventCallback>();
	}

	public String getClientId() {
		return clientId;
	}

	/**
	 * Method to register callbacks to different Game Client events.
	 * @param type
	 * @param callback
	 */
	public void registerCallback(CallbackType type, ClientEventCallback callback) {
		if(type == null) return;
		callbackMap.put(type, callback);
	}

	/**
	 * Method to async look for game servers
	 */
	public void searchForGameServers() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				hostAddress = IPUtils.scanForGameServer(port);
				NetClientConnector clientConnector = new NetClientConnector(null, hostAddress, port);
				clientId = (String)clientConnector.processNetCommand(NetCommand.CONNECT, null);
				ClientEventCallback callback = callbackMap.get(CallbackType.CONNECTION);
				if(callback != null) {
					if(clientId != null) callback.handleEvent(ClientEvent.CONNECTION_SUCCESS);
					else callback.handleEvent(ClientEvent.CONNECTION_FAILURE);
				}
				Log.i(TAG, "Connected to server, UUID: " + clientId);
			}
		}).start();
	}

	/**
	 * Method to async register player with server
	 * @param player
	 */
	public void registerPlayer(final Player player) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				NetClientConnector clientConnector = new NetClientConnector(null, hostAddress, port);
				Object output = clientConnector.processNetCommand(NetCommand.WRITE, player);
				ClientEventCallback callback = callbackMap.get(CallbackType.PLAYER_CREATION);
				if(callback != null) {
					if(output != null) callback.handleEvent(ClientEvent.PLAYER_CREATED_SUCCESS);
					else callback.handleEvent(ClientEvent.PLAYER_CREATED_SUCCESS);
				}
				Log.i(TAG, "Created Player and registered with server.");
			}
		}).start();
	}

}
