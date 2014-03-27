package com.king.drunk.media.router;

/**
 * Created by king on 2/17/14.
 *
 * Interface to provide callbacks for when chromecast state changes.
 */
public interface GameCastConnectedCallback {
	public void connectionSuccess();
	public void connectionFail();
	public void disconnected();
}
