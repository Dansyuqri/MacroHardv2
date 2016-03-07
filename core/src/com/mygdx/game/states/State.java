package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Syuqri on 3/7/2016.
 */
public abstract class State {
    protected OrthographicCamera cam;
    protected Vector3 touch;
    protected GameStateManager gsm;

    protected State(GameStateManager gsm) {
        this.gsm = gsm;
        cam = new OrthographicCamera();
        touch = new Vector3();
    }

    protected abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();


}
