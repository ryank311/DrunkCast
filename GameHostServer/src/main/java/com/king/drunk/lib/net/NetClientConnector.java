package com.king.drunk.lib.net;

import android.util.Log;

import com.king.drunk.model.net.NetCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by king on 2/19/14.
 */
public class NetClientConnector {

	private static final String TAG = NetClientConnector.class.getSimpleName();

	private String uuid;
	private String hostIp;
	private int port;

	public NetClientConnector(String uuid, String ip, int port) {
		this.uuid = uuid;
		this.hostIp = ip;
		this.port = port;
	}

	public Object processNetCommand(NetCommand command, Object input) {
		if(hostIp == null) return null;
		Object output = null;
		Socket client = null;
		try {
			client = new Socket(hostIp, port);
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(client.getInputStream()));
			NetAction action = new NetAction();
			switch(command) {
				case CONNECT:
					sendCommand(NetCommand.CONNECT, client);
					output = action.connect(reader);
					break;
				case WRITE:
					sendCommandWithData(NetCommand.WRITE, input, client);
					output = action.write(reader);
					break;
				default:
					Log.i(TAG, "Requested net command not recognized by server.  aborting.");
					break;
			}
		} catch (IOException ex) {
			Log.e(TAG, "Caught IO Exception while processing connect command", ex);
		} finally {
			if(client != null) {
				try {
					client.close();
				} catch (IOException e) {
					//ignore
				}
			}
		}
		return output;
	}

	public void sendCommand(NetCommand command, Socket socket) throws IOException {
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		Log.i(TAG, "Writing command to socket: " + command.name());
		writer.println(command.name());
		socket.shutdownOutput();
	}

	public void sendCommandWithData(NetCommand command, Object data, Socket socket) throws IOException {
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		Log.i(TAG, "Writing command to socket: " + command.name() + "; " + data.toString());
		writer.println(command.name());
		writer.println(data.toString());
		socket.shutdownOutput();
	}

}
