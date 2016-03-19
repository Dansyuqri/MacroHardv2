package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.states.GameStateManager;
import com.mygdx.game.states.MenuState;
import com.mygdx.game.states.PlayState;

public class MacroHardv2 extends ApplicationAdapter {
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;
	public static final String TITLE = "Demo";
	private GameStateManager gsm;
	private SpriteBatch batch;
	public static ActionResolver actionResolver;
	public static GameWorld gamew;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm = new GameStateManager();

		Gdx.gl.glClearColor(1, 0, 0, 1);
		gsm.push(new MenuState(gsm));
	}

	@Override
	public void render () {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());

		gsm.render(batch);
	}
	public MacroHardv2(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
		actionResolver.setGame(this);
	}
	public void multiplayerGameReady(){
		gamew = new GameWorld(this);
		gamew.multiplayer = true;
		long time = System.currentTimeMillis();
		while( System.currentTimeMillis() - time < 5000){

		}
		//Gdx.app.log("EMPEZANDO", "Starting Game");
		//gsm.set(new PlayState(gsm));
		//dispose();
		//this.actionResolver.sendPos((float) 6, (float) 6);
	}

	public void updateGameWorld(float x, float y){
		gamew = new GameWorld(this);
		gamew.px = x;
		gamew.py = y;
		if(gamew.px==6){
			//gsm.set(new PlayState(gsm));
			//dispose();
		}
	}
}
