package com.king.drunk.lib.net;

import android.util.Log;

import com.king.drunk.lib.server.ConnectionInfo;
import com.king.drunk.model.GameManager;
import com.king.drunk.model.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;

/**
 * Created by king on 2/19/14.
 */
public class NetController {

	private static final String TAG = NetController.class.getSimpleName();

	private Map<String, ConnectionInfo> connectedPlayers;
	private GameManager gameManager;


	/**
	 * Constructor for the net controller, setup state.
	 * @param connectedPlayers
	 * @param gameManager
	 */
	public NetController(Map<String, ConnectionInfo> connectedPlayers, GameManager gameManager) {
		this.connectedPlayers = connectedPlayers;
		this.gameManager = gameManager;
	}

	/**
	 * Process connect command
	 * @param writer
	 */
	public void doConnect(PrintWriter writer) throws IOException{
		if(connectedPlayers.size() > 8) {
			ConnectionInfo oldest = null;
			for(Map.Entry<String, ConnectionInfo> entry : connectedPlayers.entrySet()) {
				if(oldest == null) oldest = entry.getValue();
				else if (entry.getValue().getLastConnectionTime() < oldest.getLastConnectionTime())
					oldest = entry.getValue();
			}
			connectedPlayers.remove(oldest.getConnectionId());
			Log.i(TAG, "Booted player: " + oldest.getConnectionId());
		}

		UUID uuid = UUID.randomUUID();
		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setConnectionId(uuid.toString());
		connectedPlayers.put(uuid.toString(), connectionInfo);
		Log.i(TAG, "Connected player: " + connectionInfo.getConnectionId() + " @ "
				+ connectionInfo.getLastConnectionTime());
		writer.println(uuid.toString());
	}

	public void doWrite(BufferedReader reader, PrintWriter writer) throws IOException {
		String playerString = reader.readLine();
		if(playerString == null) {
			Log.i(TAG, "Failed to read player from Stream");
		}
		Player player = new Player(playerString);
		Log.i(TAG, "Created Player for client: " + player.toString());
		gameManager.registerPlayer(player);
	}

}
