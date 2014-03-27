package com.king.drunk.lib.client;

import com.king.drunk.lib.client.event.ClientEvent;

/**
 * Created by king on 3/3/14.
 */
public interface ClientEventCallback {
	public void handleEvent(ClientEvent e);
}
