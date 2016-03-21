package com.mygdx.game;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.example.games.basegameutils.GameHelper;
import com.mygdx.game.MacroHardv2;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import com.mygdx.game.ActionResolver;

public class AndroidLauncher extends AndroidApplication implements ActionResolver{
	private GSGameHelper _gameHelper;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_gameHelper = new GSGameHelper(this, GameHelper.CLIENT_GAMES);
		_gameHelper.enableDebugLog(false);

		GameHelperListener gameHelperListerner = new GameHelper.GameHelperListener() {

			@Override
			public void onSignInSucceeded() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSignInFailed() {
				// TODO Auto-generated method stub

			}
		};
		_gameHelper.setup(gameHelperListerner);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;

		initialize(new MacroHardv2(this), config);
		Gdx.graphics.requestRendering();
	}

	@Override
	public void SignIn() {
		try{
			runOnUiThread(new Runnable(){
				public void run(){
					_gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e){
			Gdx.app.log("CIRUS", "Google Services Login Failed " + e.getMessage());
		}

	}

	@Override
	public void SignOut() {
		try{
			runOnUiThread(new Runnable(){
				public void run(){
					_gameHelper.signOut();
				}
			});
		}
		catch (Exception e){
			Gdx.app.log("CIRUS", "Google Services Logout Failed " + e.getMessage());
		}

	}

	@Override
	public boolean isSignedIn() {
		return _gameHelper.isSignedIn();
	}

	@Override
	public void QuickGame(){
		try{
			runOnUiThread(new Runnable(){
				public void run(){
					_gameHelper.quickGame();
				}
			});
		}
		catch (Exception e){
			Gdx.app.log("CIRUS", "Google Services Logout Failed " + e.getMessage());
		}
	}

	@Override
	public void initMatch(){
		_gameHelper.initMatch();
	}

	@Override
	public void onStart(){
		super.onStart();
		_gameHelper.onStart(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		_gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		_gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void setGame(MacroHardv2 game) {
		_gameHelper.setGame(game);
	}

	public void sendPos(float x,float y){
		_gameHelper.sendPos(x, y);
	}
	public void sendMap(boolean[] map){
		_gameHelper.sendMap(map);
	}
	public String gethostid(){
		return _gameHelper.host;
	}
	public String getyourid(){
		return _gameHelper.mMyId;
	}
}
