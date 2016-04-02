package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.states.GameStateManager;
import com.mygdx.game.states.MenuState;
import com.mygdx.game.states.PlayState;
import com.mygdx.game.states.PlayStateHost;
import com.mygdx.game.states.PlayStateNonHost;

import java.awt.Menu;

import static java.lang.Thread.sleep;

public class MacroHardv2 extends ApplicationAdapter{
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;
	public static final String TITLE = "Demo";
	private GameStateManager gsm;
	private SpriteBatch batch;
	public static ActionResolver actionResolver;
	
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
		gsm.render(batch);
	}

	public MacroHardv2(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
		actionResolver.setGame(this);
	}

	public void multiplayerGameReady(){

		if(!this.actionResolver.gethostid().equals(this.actionResolver.getyourid())){
			MenuState.gotoPlayP = true;
		} else{
			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			MenuState.goToPlay=true;
		}

	}
	public GameStateManager getGsm() {
		return gsm;
	}
}
