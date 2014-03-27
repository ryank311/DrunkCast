package com.king.drunk.lib;

import android.util.Log;

import com.king.drunk.lib.net.NetController;
import com.king.drunk.lib.server.ConnectionInfo;
import com.king.drunk.media.router.GameCastWrapper;
import com.king.drunk.model.GameManager;
import com.king.drunk.model.HostGameManager;
import com.king.drunk.model.net.NetCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by king on 2/18/14.
 */
public class GameServer implements Runnable {

	private static final String TAG = GameServer.class.getSimpleName();

	private boolean started = false;
	private int port = 39249;

	private Map<String, ConnectionInfo> connectedPlayers;
	private GameManager gameManager;

	public GameServer(GameCastWrapper gameCast) {
		resetServer(gameCast);
	}

	@Override
	public void run() {
		Log.i(TAG, "Game Server Starting up on port: " + port);
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);  //Server socket
		} catch (IOException e) {
			Log.e(TAG, "Game Server failed to start on port: " + port, e);
			return;
		}
		while(started) {
			synchronized (this) {
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
					BufferedReader reader =
							new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					processRequest(clientSocket, reader, clientSocket.getOutputStream());
				} catch (IOException e) {
					Log.e(TAG, "Socket connection interrupted", e);
					shutDown();
				} finally {
					try {
						if(clientSocket != null)
							clientSocket.close();
					} catch (IOException e) {
						//Ignore
					}
				}
			}
		}
	}

	/**
	 * Method to read and delegate request
	 */
	public void processRequest(Socket client, BufferedReader reader, OutputStream out) {
		try {
			String request = reader.readLine();
			if(request == null) {
				Log.e(TAG, "Failed to process net command, request not received");
				return;
			}
			PrintWriter writer = new PrintWriter(out, true);
			NetCommand command = NetCommand.valueOf(request);
			NetController controller = new NetController(connectedPlayers, gameManager);
			switch(command) {
				case CONNECT :
					controller.doConnect(writer);
					break;
				case WRITE :
					controller.doWrite(reader, writer);
					break;
				default:
					Log.e(TAG, "Failed to match net command");
			}
			client.shutdownInput();
			client.shutdownOutput();
		} catch (IOException e) {
			Log.e(TAG, "Failed to read net command", e);
		}
	}

	/**
	 * Indicator as to wheter the server is started
	 * @return
	 */
	public synchronized boolean isStarted() {
		return started;
	}

	/**
	 * Initialize the server
	 */
	public void resetServer(GameCastWrapper gameCast) {
		Log.i(TAG, "Creating a new game State");
		connectedPlayers = new HashMap<String, ConnectionInfo>();
		gameManager = new HostGameManager(gameCast);
		started = true;
	}

	/**
	 * Shutdown the server after the current operation
	 */
	public synchronized void shutDown() {
		Log.i(TAG, "Server.shutDown() called.  Shutting down the server.");
		started = false;
	}
}
