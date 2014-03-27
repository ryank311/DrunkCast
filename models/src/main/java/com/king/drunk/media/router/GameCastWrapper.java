package com.king.drunk.media.router;

import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;

/**
 * Created by king on 2/17/14.
 */
public class GameCastWrapper {

	private MediaRouter mMediaRouter;
	private MediaRouterCallback mMediaRouterCallback;
	private MediaRouteSelector mMediaRouteSelector;

	/**
	 * Construct a game cast wrapper object
	 * @param mediaRouter
	 * @param mediaRouterCallback
	 * @param selector
	 */
	public GameCastWrapper(MediaRouter mediaRouter,
	                       MediaRouterCallback mediaRouterCallback,
	                       MediaRouteSelector selector) {
		this.mMediaRouterCallback = mediaRouterCallback;
		this.mMediaRouter = mediaRouter;
		this.mMediaRouteSelector = selector;
	}

	/**
	 * Teardown the callbacks.
	 */
	public void teardown() {
		mMediaRouterCallback.teardown();
	}

	/**
	 * Simple API to start scanning for chromecasts
	 */
	public void startScan() {
		mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
				MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
	}

	/**
	 * Simple API to stop scan for chromecasts
	 */
	public void stopScan() {
		mMediaRouter.removeCallback(mMediaRouterCallback);
	}

	/**
	 * Simple API to register the route selector for the menu button.
	 * @param mediaRouteActionProvider
	 */
	public void setupProvider(MediaRouteActionProvider mediaRouteActionProvider) {
		mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
	}

	/**
	 * API for sending a message to the chrome cast client.  This message
	 * consists of a net command + some data object that will be marshalled with the
	 * toString() method on the object.
	 * @param message
	 */
	public void sendMessage(Object message) {
		mMediaRouterCallback.sendMessage(message.toString());
	}
}
