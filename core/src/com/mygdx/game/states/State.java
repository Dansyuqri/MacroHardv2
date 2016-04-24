package com.mygdx.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;


/**
 * Created by Syuqri on 3/7/2016.
 */

/**
 * This abstract class allows the extended States to inherit the methods implemented
 */
public abstract class State{
    protected OrthographicCamera cam;
    protected Vector3 touch;
    protected GameStateManager gsm;

    protected State(GameStateManager gsm) {
        this.gsm = gsm;
        cam = new OrthographicCamera();
        cam.setToOrtho(false, 480, 800);
        touch = new Vector3();
    }

    protected abstract void handleInput();
    public abstract void update(byte[] message);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();


}
