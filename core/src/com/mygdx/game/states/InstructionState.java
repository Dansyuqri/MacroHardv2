package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Syuqri on 3/19/2016.
 */
public class InstructionState extends State{
    private static String[] instructionPages = {"pageOne.png","pageTwo.png","pageThree.png"};
    private int pageIndex = 0;
    private final int maxIndex = 2;
    private float graphicsX, graphicsY;
    private Texture page;
    private Vector3 touchPos = new Vector3(0,0,0);
    private boolean touched = false;
    private boolean wasBackButton = false;
    public InstructionState(GameStateManager gsm){
        super(gsm);
        cam = new OrthographicCamera();
        cam.setToOrtho(false,480,800);
        graphicsX = Gdx.graphics.getWidth();
        graphicsY = Gdx.graphics.getHeight();
        page = new Texture(instructionPages[pageIndex]);
        Gdx.input.setCatchBackKey(true);

    }
    @Override
    protected void handleInput() {
        touchPos.x = Gdx.input.getX();
        touchPos.y = Gdx.input.getY();
        if(Gdx.input.isKeyPressed(Keys.BACK)){
            wasBackButton = true;
        }
        if(Gdx.input.isTouched()){
            touched=true;
        }
        if(!Gdx.input.isTouched() && touched){
            if(touchPos.x>graphicsX/2){
                pageIndex++;
                if(pageIndex <=maxIndex) {
                    page.dispose();
                    page = new Texture(instructionPages[pageIndex]);
                }
                else if(pageIndex > maxIndex) {
                    gsm.set(new MenuState(gsm));
                    dispose();
                }
            }
            else if(touchPos.x<graphicsX/2){
                pageIndex--;
                if(pageIndex >=0) {
                    page.dispose();
                    page = new Texture(instructionPages[pageIndex]);
                }
                else if(pageIndex < 0) {
                    gsm.set(new MenuState(gsm));
                    dispose();
                }
            }
            touched = false;
        }
        else if(!Gdx.input.isKeyPressed(Keys.BACK) && wasBackButton){
            pageIndex--;
            if(pageIndex >=0) {
                page.dispose();
                page = new Texture(instructionPages[pageIndex]);
            }
            else if(pageIndex < 0) {
                gsm.set(new MenuState(gsm));
                dispose();
            }
            wasBackButton = false;
        }
    }

    @Override
    public void update(byte[] message) {}

    @Override
    public void render(SpriteBatch sb) {
        handleInput();
        sb.begin();
        sb.draw(page, 0, 0, graphicsX, graphicsY);
        sb.end();
    }

    @Override
    public void dispose() {
        page.dispose();
    }
}