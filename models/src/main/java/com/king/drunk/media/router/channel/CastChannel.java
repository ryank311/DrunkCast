package com.king.drunk.media.router.channel;

import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;

/**
 * Created by king on 2/17/14.
 */
public class CastChannel implements Cast.MessageReceivedCallback {

	private static final String TAG = CastChannel.class.getSimpleName();

	private String namespace;

	public CastChannel(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * @return custom namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/*
	 * Receive message from the receiver app
	 */
	@Override
	public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
		Log.d(TAG, "onMessageReceived: " + message);
	}
}
