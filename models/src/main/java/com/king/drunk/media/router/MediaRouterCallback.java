package com.king.drunk.media.router;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.king.drunk.media.router.channel.CastChannel;

import java.io.IOException;

/**
 * Created by king on 2/17/14.
 */
public class MediaRouterCallback extends MediaRouter.Callback
		implements GoogleApiClient.OnConnectionFailedListener,
		GoogleApiClient.ConnectionCallbacks {

	private static final String TAG = MediaRouterCallback.class.getSimpleName();

	private Context appContext;
	private GameCastConnectedCallback mConnectedCallback;

	private CastDevice mSelectedDevice;
	private Cast.Listener mCastListener;
	private GoogleApiClient mApiClient;
	private CastChannel mCastChannel;

	private String appId;
	private String namespace;
	private boolean mApplicationStarted;
	private boolean mWaitingForReconnect;


	public MediaRouterCallback(
			Context appContext,
			GameCastConnectedCallback callback,
			String appId,
			String namespace) {
		this.appContext = appContext;
		this.mConnectedCallback = callback;
		this.appId = appId;
		this.namespace = namespace;
	}

	@Override
	public void onRouteSelected(MediaRouter router, RouteInfo info) {
		// Handle the user route selection.
		mSelectedDevice = CastDevice.getFromBundle(info.getExtras());
		launchReceiver();
	}

	@Override
	public void onRouteUnselected(MediaRouter router, RouteInfo info) {
		teardown();
		mSelectedDevice = null;
	}

	/**
	 * Method to be invoked when a given device is selected
	 */
	protected void launchReceiver() {
		try {
			mCastListener = new Cast.Listener() {
				@Override
				public void onApplicationDisconnected(int errorCode) {
					Log.d(TAG, "application has stopped");
					teardown();
				}
			};
			// Connect to Google Play services
			Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
					.builder(mSelectedDevice, mCastListener);
			mApiClient = new GoogleApiClient.Builder(appContext)
					.addApi(Cast.API, apiOptionsBuilder.build())
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();

			mApiClient.connect();
		} catch (Exception e) {
			Log.e(TAG, "Failed launchReceiver", e);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		mConnectedCallback.connectionFail();
		teardown();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (mApiClient == null) {
			// We got disconnected while this runnable was pending
			// execution.
			mConnectedCallback.disconnected();
			return;
		}

		try {
			if (mWaitingForReconnect) {
				mWaitingForReconnect = false;

				// Check if the receiver app is still running
				if ((connectionHint != null)
						&& connectionHint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
					teardown();
				} else {
					// Re-create the custom message channel
					try {
						Cast.CastApi.setMessageReceivedCallbacks(mApiClient,
								mCastChannel.getNamespace(),
								mCastChannel);
					} catch (IOException e) {
						Log.e(TAG, "Exception while creating channel", e);
						mConnectedCallback.connectionFail();
					}
				}
			} else {
				// Launch the receiver app
				Cast.CastApi
						.launchApplication(mApiClient,
								appId, false)
						.setResultCallback(
								new ResultCallback<Cast.ApplicationConnectionResult>() {
									@Override
									public void onResult(
											Cast.ApplicationConnectionResult result) {
										Status status = result.getStatus();
										Log.d(TAG,
												"ApplicationConnectionResultCallback.onResult: statusCode"
														+ status.getStatusCode());
										if (status.isSuccess()) {
											mApplicationStarted = true;
											mCastChannel = new CastChannel(namespace);
											try {
												Cast.CastApi.setMessageReceivedCallbacks(
														mApiClient,
														mCastChannel.getNamespace(),
														mCastChannel);
											} catch (IOException e) {
												Log.e(TAG, "Exception while creating channel", e);
											}
											mConnectedCallback.connectionSuccess();
										} else {
											Log.e(TAG, "application could not launch");
											mConnectedCallback.connectionFail();
											teardown();
										}
									}
								});
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed to launch application", e);
			mConnectedCallback.connectionFail();
		}
	}

	/**
	 * Send a text message to the receiver
	 *
	 * @param message
	 */
	public void sendMessage(String message) {
		if (mApiClient != null && mCastChannel != null) {
			try {
				Cast.CastApi.sendMessage(mApiClient,
						mCastChannel.getNamespace(), message)
						.setResultCallback(new ResultCallback<Status>() {
							@Override
							public void onResult(Status result) {
								if (!result.isSuccess()) {
									Log.e(TAG, "Sending message failed");
								}
							}
						});
			} catch (Exception e) {
				Log.e(TAG, "Exception while sending message", e);
			}
		} else {
			Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Method to be invoked when we want to close connection to device.
	 */
	public void teardown() {
		if (mApiClient != null) {
			if (mApplicationStarted) {
				try {
					Cast.CastApi.stopApplication(mApiClient);
					if (mCastChannel != null) {
						Cast.CastApi.removeMessageReceivedCallbacks(
								mApiClient,
								mCastChannel.getNamespace());
						mCastChannel = null;
					}
				} catch (IOException e) {
					Log.e(TAG, "Exception while removing channel", e);
				}
				mApplicationStarted = false;
			}
			if (mApiClient.isConnected()) {
				mApiClient.disconnect();
			}
			mApiClient = null;
		}
		mSelectedDevice = null;
		mWaitingForReconnect = false;
		mConnectedCallback.disconnected();
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mWaitingForReconnect = true;
		mConnectedCallback.disconnected();
	}

	public CastDevice getSelectedDevice() {
		return mSelectedDevice;
	}

	public void setSelectedDevice(CastDevice mSelectedDevice) {
		this.mSelectedDevice = mSelectedDevice;
	}

	public boolean isConnectedAndStarted() {
		return mApplicationStarted;
	}
}
