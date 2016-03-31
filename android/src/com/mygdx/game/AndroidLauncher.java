package com.mygdx.game;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.google.example.games.basegameutils.GameHelper;

import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

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
	public void Inviteplayers(){
		try{
			runOnUiThread(new Runnable(){
				public void run(){
					_gameHelper.inviteplayers();
				}
			});
		}
		catch (Exception e){
			Gdx.app.log("CIRUS", "Google Services Logout Failed " + e.getMessage());
		}
	}
	public void Seeinvites(){
		try{
			runOnUiThread(new Runnable(){
				public void run(){
					_gameHelper.seeinvites();
				}
			});
		}
		catch (Exception e){
			Gdx.app.log("CIRUS", "Google Services Logout Failed " + e.getMessage());
		}
	}
	public void Acceptinvites(){
		try{
			runOnUiThread(new Runnable(){
				public void run(){
					_gameHelper.acceptinvites();
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
	public void sendPing(byte[] ping){_gameHelper.sendPing(ping);}
	public void sendReliable(byte[] message){
		_gameHelper.sendReliable(message);
	}

	public String gethostid(){
		return _gameHelper.host;
	}
	public String getyourid(){
		return _gameHelper.mMyId;
	}
	public int getmyidint(){
		return _gameHelper.myidno;
	}
}
