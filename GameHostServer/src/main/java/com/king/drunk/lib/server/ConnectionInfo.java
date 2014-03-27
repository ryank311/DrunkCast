package com.king.drunk.lib.server;

import com.king.drunk.model.entity.Player;

/**
 * Created by king on 2/19/14.
 */
public class ConnectionInfo {
	private long lastConnectionTime;
	private String connectionId;
	private String ipAddress;
	private Player connectedPlayer;

	public ConnectionInfo() {
		lastConnectionTime = System.currentTimeMillis();
	}

	public void ping() {
		lastConnectionTime = System.currentTimeMillis();
	}

	public long getLastConnectionTime() {
		return lastConnectionTime;
	}

	public void setLastConnectionTime(long lastConnectionTime) {
		this.lastConnectionTime = lastConnectionTime;
	}

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Player getConnectedPlayer() {
		return connectedPlayer;
	}

	public void setConnectedPlayer(Player connectedPlayer) {
		this.connectedPlayer = connectedPlayer;
	}
}
