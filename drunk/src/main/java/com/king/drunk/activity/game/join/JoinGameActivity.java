package com.king.drunk.activity.game.join;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.king.drunk.R;
import com.king.drunk.activity.game.host.HostGameActivity;
import com.king.drunk.lib.client.ClientEventCallback;
import com.king.drunk.lib.client.event.ClientEvent;
import com.king.drunk.model.AppGameManager;
import com.king.drunk.util.IntentExtraIdentifiers;
import com.king.drunk.view.ColorGridAdapter;

public class JoinGameActivity extends Activity {
	protected ColorGridAdapter gridAdapter;
	protected boolean hostGame = false;
	AppGameManager appGameManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);
		init();
	}

	/**
	 * Method to initialize the GridView object
	 */
	protected void init() {
		GridView gridView = (GridView) findViewById(R.id.join_grid_view);
		gridAdapter = new ColorGridAdapter(this);
		gridView.setAdapter(gridAdapter);
		gridView.setOnItemClickListener(gridAdapter);

		hostGame = IntentExtraIdentifiers.START_GAME_TYPE_HOST.equals(
				getIntent().getStringExtra(IntentExtraIdentifiers.START_GAME_EXTRA));
		//Setup strings for hosting game
		if(hostGame) {
			getActionBar().setTitle(getResources().getString(R.string.title_activity_host_game));
		}
		appGameManager = AppGameManager.getInstance();
	}

	/**
	 * Join game button clicked
	 * @param v
	 */
	public void doJoinGame(final View v) {

		EditText playerNameText = (EditText) this.findViewById(R.id.playerNameText);
		Editable editable = playerNameText.getText();
		String playerName = editable.toString();
		int playerColor = gridAdapter.getColor();
		if(gridAdapter.validateSelection() && !validate(playerName)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.validation_failed_title))
					.setMessage(getString(R.string.validation_failed_text))
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//do things
						}
					});;
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}
		final ProgressDialog pleaseWait = ProgressDialog.show(this, getString(R.string.joining_game), getString(R.string.please_wait), true);
		appGameManager.registerPlayerRegistrationCallback(new ClientEventCallback() {
			@Override
			public void handleEvent(final ClientEvent e) {
				v.post(new Runnable() {
					@Override
					public void run() {
						switch(e) {
							case PLAYER_CREATED_FAILURE:
								pleaseWait.dismiss();
								Toast.makeText(JoinGameActivity.this, getString(R.string.connection_failed_text), 2000).show();
								break;
							case PLAYER_CREATED_SUCCESS:
								pleaseWait.dismiss();
								Toast.makeText(JoinGameActivity.this, getString(R.string.connection_success_text), 2000).show();
								if(hostGame) {
									Intent target = new Intent(JoinGameActivity.this, HostGameActivity.class);
									startActivity(target);
								} else {
									Intent target = new Intent(JoinGameActivity.this, WaitForHostActivity.class);
									startActivity(target);
								}
								break;
						}
					}
				});
			}
		});
		appGameManager.registerPlayer(playerName, playerColor);
	}

	/**
	 * Method to validate the fields in this activity.
	 */
	protected boolean validate(String name) {
		return name != null;
	}
}
