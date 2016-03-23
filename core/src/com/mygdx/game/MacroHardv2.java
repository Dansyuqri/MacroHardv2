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
		//gamew.multiplayer = true;
		gsm.set(new PlayStateHost(gsm));
		dispose();

		//Used to send players coorindates to everyone else
		//this.actionResolver.sendPos((float) 1, (float) 1);
		//used to send map coordinates to everyone else
		//boolean[] a = {false, false, true, false, false, true, false, false, true};
		//this.actionResolver.sendMap(a);
		//to get hostid
		//if(this.actionResolver.gethostid().equals(this.actionResolver.getyourid())){
		//	gsm.set(new PlayStateHost(gsm));
		//	dispose();
		//}



	}

	//Used to receive player's cooridinates
	public void updateGameWorld(float x, float y){
		gamew = new GameWorld(this);
		gamew.px = x;
		gamew.py = y;
		System.out.println("Receiveda");
		if (gamew.px == 1){
			gsm.set(new PlayStateHost(gsm));
			dispose();
		}
	}

	//Used to receive for Map Generation, Boolean array of size 9
	public void updateMapWorld(float a, float b,float c, float d,float e, float f,float g, float h,float i){
		gamew = new GameWorld(this);
		gamew.x = a;
		gamew.y = b;
		gamew.px = c;
		gamew.py = d;
		gamew.pa = e;
		gamew.pb = f;
		gamew.pc = g;
		gamew.pd = h;
		gamew.pe = i;
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		System.out.println(d);
		System.out.println(e);
		System.out.println(f);
		System.out.println(g);
		System.out.println(h);
		System.out.println(i);
	}

}
