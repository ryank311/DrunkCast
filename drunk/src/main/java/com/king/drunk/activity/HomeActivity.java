package com.king.drunk.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.king.drunk.R;
import com.king.drunk.activity.game.join.JoinGameActivity;
import com.king.drunk.lib.client.ClientEventCallback;
import com.king.drunk.lib.client.event.ClientEvent;
import com.king.drunk.media.router.GameCastConnectedCallback;
import com.king.drunk.media.router.MediaRouterCallback;
import com.king.drunk.model.AppGameManager;
import com.king.drunk.util.IntentExtraIdentifiers;

public class HomeActivity extends ActionBarActivity implements GameCastConnectedCallback {

	private AppGameManager appGameManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		appGameManager = AppGameManager.getInstance();
		appGameManager.setupCast(
				new MediaRouterCallback(
						this,
						this,
						getString(R.string.app_id),
						getString(R.string.app_namespace)),
				this);
	}

	/**
	 * Host Game button is clicked.
	 * @param v
	 */
	public void hostGame(final View v) {
		final ProgressDialog pleaseWait = ProgressDialog.show(this, getString(R.string.hosting_game), getString(R.string.please_wait), true);
		appGameManager = AppGameManager.getInstance();
		appGameManager.hostGame();
		appGameManager.registerGameConnectedCallback(new ClientEventCallback() {
			@Override
			public void handleEvent(ClientEvent e) {
				if (ClientEvent.CONNECTION_SUCCESS == e) {
					v.post(new Runnable() {
						@Override
						public void run() {
							pleaseWait.dismiss();
							Toast.makeText(HomeActivity.this, getString(R.string.connection_success_text), 2000).show();
							Intent target = new Intent(HomeActivity.this, JoinGameActivity.class);
							target.putExtra(IntentExtraIdentifiers.START_GAME_EXTRA, IntentExtraIdentifiers.START_GAME_TYPE_HOST);
							startActivity(target);
						}
					});
				} else {
					v.post(new Runnable() {
						@Override
						public void run() {
							pleaseWait.dismiss();
							Toast.makeText(HomeActivity.this, getString(R.string.connection_failed_text), 2000).show();
						}
					});
				}
			}
		});
		appGameManager.joinGame();
	}

	/**
	 * Join Game Button is Clicked
	 *
	 * added some callbacks to your callbacks
	 *
	 * @param v
	 */
	public void joinGame(final View v) {
		final ProgressDialog pleaseWait = ProgressDialog.show(this, getString(R.string.joining_game), getString(R.string.please_wait), true);
		appGameManager = AppGameManager.getInstance();
		appGameManager.registerGameConnectedCallback(new ClientEventCallback() {
			@Override
			public void handleEvent(final ClientEvent e) {
				v.post(new Runnable() {
					@Override
					public void run() {
						switch (e) {
							case CONNECTION_SUCCESS:
								pleaseWait.dismiss();
								Toast.makeText(HomeActivity.this, getString(R.string.connection_success_text), 2000).show();
								Intent target = new Intent(HomeActivity.this, JoinGameActivity.class);
								target.putExtra(IntentExtraIdentifiers.START_GAME_EXTRA, IntentExtraIdentifiers.START_GAME_TYPE_JOIN);
								startActivity(target);
								break;
							case CONNECTION_FAILURE:
								pleaseWait.dismiss();
								Toast.makeText(HomeActivity.this, getString(R.string.connection_failed_text), 2000).show();
								break;
						}
					}
				});
			}
		});
		appGameManager.joinGame();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.home, menu);
		MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
		appGameManager.getGameCast().setupProvider((MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem));
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Start media router discovery
		appGameManager.getGameCast().startScan();
	}

	@Override
	protected void onPause() {
		if (isFinishing()) {
			// End media router discovery
			appGameManager.getGameCast().stopScan();
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		appGameManager.getGameCast().teardown();
		super.onDestroy();
	}

	@Override
	public void connectionSuccess() {
		final Button host = (Button) findViewById(R.id.host_button);
		host.setVisibility(View.VISIBLE);
		final Button join = (Button) findViewById(R.id.join_button);
		join.setEnabled(false);
	}

	@Override
	public void connectionFail() {
		Toast.makeText(this, getString(R.string.chromecast_connection_failed_text), 2000).show();
	}

	@Override
	public void disconnected() {
		final Button host = (Button) findViewById(R.id.host_button);
		host.setVisibility(View.INVISIBLE);
		final Button join = (Button) findViewById(R.id.join_button);
		join.setEnabled(true);
	}
}
