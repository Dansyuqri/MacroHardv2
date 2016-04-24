package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.customEnum.StateType;

/**
 * Created by Syuqri on 21-Apr-16.

 /**
 * This state is solely to be used for the initial start up screen of the game
 * This displays the company logo
 */
public class SplashState extends State {

    private Texture splashScreen;
    private float graphicsX, graphicsY;
    private static int timer = 0;
    public SplashState(GameStateManager gsm){
        super(gsm);
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        graphicsX = Gdx.graphics.getWidth();
        graphicsY = Gdx.graphics.getHeight();

        splashScreen = new Texture("splashScreen.png");

    }
    @Override
    protected void handleInput() {

    }

    @Override
    public void update(byte[] message) {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        cam.update();
        sb.setProjectionMatrix(cam.combined);
        sb.draw(splashScreen, 0, 0, graphicsX, graphicsY);
        sb.end();
        timer++;
        if(timer > 150){
            gsm.set(new MenuState(gsm),StateType.NON_PLAY);
            dispose();
        }
    }

    @Override
    public void dispose() {
        splashScreen.dispose();
    }
}
