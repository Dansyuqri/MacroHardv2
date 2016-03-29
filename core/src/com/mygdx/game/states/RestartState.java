package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Syuqri on 3/23/2016.
 */
public class RestartState extends State{
    private Texture background;
    private Vector3 touchPos = new Vector3(0,0,0);
    private float bufferFromBottom = 200;
    private boolean touched = false;

    //Resize variables
    private float graphicsX, graphicsY;

    public RestartState(GameStateManager gsm){
        super(gsm);
        background = new Texture("menu_bg.png");

        graphicsX = Gdx.graphics.getWidth();
        graphicsY = Gdx.graphics.getHeight();

    }
    @Override
    protected void handleInput() {
        touchPos.x = Gdx.input.getX();
        touchPos.y = Gdx.input.getY();
    }

    @Override
    public void update(byte[] message) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, graphicsX, graphicsY);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}