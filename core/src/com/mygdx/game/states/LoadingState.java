package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Syuqri on 21-Apr-16.
 */
public class LoadingState extends State{
    /**
     * This allows for the loading screen to be implemented where necessary
     */
    private Texture loadingScreenImage;
    private float graphicsX, graphicsY;
    private static volatile int timer = 0;
    private final String[] loadingScreenNames = {"1.jpg","2.jpg","3.jpg","4.jpg"};

    public LoadingState(GameStateManager gsm){
        super(gsm);

        Gdx.input.setCatchBackKey(true);

        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        graphicsX = Gdx.graphics.getWidth();
        graphicsY = Gdx.graphics.getHeight();

        loadingScreenImage = new Texture(loadingScreenNames[0]);

    }
    @Override
    protected void handleInput() {

    }

    @Override
    public void update(byte[] message) {

    }

    @Override
    public void render(SpriteBatch sb) {

        //This cycles through the loadingScreenImage array in order to animate the loading screen
        sb.begin();
        cam.update();
        sb.setProjectionMatrix(cam.combined);
        if(timer==0){
            timer++;
            loadingScreenImage.dispose();
            loadingScreenImage = new Texture(loadingScreenNames[0]);
        }
        else if(timer==1){
            timer++;
            loadingScreenImage.dispose();
            loadingScreenImage = new Texture(loadingScreenNames[1]);
        }
        else if(timer==2){
            timer++;
            loadingScreenImage.dispose();
            loadingScreenImage = new Texture(loadingScreenNames[2]);
        }
        else if(timer==3){
            timer++;
            loadingScreenImage.dispose();
            loadingScreenImage = new Texture(loadingScreenNames[3]);
        }
        else{
            timer=0;
            loadingScreenImage.dispose();
            loadingScreenImage = new Texture(loadingScreenNames[0]);
        }
        sb.draw(loadingScreenImage, 0, 0, graphicsX, graphicsY);
        sb.end();
    }

    @Override
    public void dispose() {

    }
}
