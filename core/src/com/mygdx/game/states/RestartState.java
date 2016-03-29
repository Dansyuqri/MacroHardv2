package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.objects.CustomButton;

/**
 * Created by Syuqri on 3/23/2016.
 */
public class RestartState extends State{
    private Texture background, playAgainBtnImage, mainMenuBtnImage;
    private CustomButton playAgainBtn, mainMenuBtn;

    private Vector3 touchPos = new Vector3(0,0,0);
    private float bufferFromTop = 200;
    private boolean touched = false;

    //Resize variables
    private float graphicsX, graphicsY,
            playAgainBtnX, playAgainBtnY,
            mainMenuBtnX, mainMenuBtnY;

    public RestartState(GameStateManager gsm){
        super(gsm);
        cam = new OrthographicCamera();
        cam.setToOrtho(false, 480, 800);

        graphicsX = Gdx.graphics.getWidth();
        graphicsY = Gdx.graphics.getHeight();

        generateTextures();

        playAgainBtnX = playAgainBtnImage.getWidth()*3;
        playAgainBtnY = playAgainBtnImage.getHeight()*3;
        playAgainBtn = new CustomButton( (graphicsX / 2) - (playAgainBtnX / 2), graphicsY / 2 - playAgainBtnY / 2, playAgainBtnX, playAgainBtnY);
        playAgainBtn.setImage(playAgainBtnImage);

        mainMenuBtnX = mainMenuBtnImage.getWidth()*3;
        mainMenuBtnY = mainMenuBtnImage.getHeight()*3;
        mainMenuBtn = new CustomButton((graphicsX/ 2) - (mainMenuBtnX/2), graphicsY/2 - playAgainBtnY/2 - mainMenuBtnY - bufferFromTop, mainMenuBtnX, mainMenuBtnY);
        mainMenuBtn.setImage(mainMenuBtnImage);

    }
    @Override
    protected void handleInput() {
        touchPos.x = Gdx.input.getX();
        touchPos.y = Gdx.graphics.getHeight() - Gdx.input.getY();

        /****************************************************************************************
         * This is a set of conditions to handle the highlighting of the buttons when pressed
         * **************************************************************************************
         */
        if (Gdx.input.isTouched() && !touched){
            if(playAgainBtn.contains(touchPos.x,touchPos.y)){
                playAgainBtnImage.dispose();
                playAgainBtnImage = new Texture("playAgainBtn_pressed.png");
                touched = true;
            }
            else if(mainMenuBtn.contains(touchPos.x,touchPos.y)){
                System.out.println(true);
                mainMenuBtnImage.dispose();
                mainMenuBtnImage = new Texture("mainMenuBtn_pressed.png");
                touched = true;
            }
        }

        else if(!Gdx.input.isTouched() && touched){
            if(playAgainBtn.contains(touchPos.x,touchPos.y)){
                //TODO: add restart game here
                dispose();
                touched = false;
            }
            else if(mainMenuBtn.contains(touchPos.x,touchPos.y)){
                gsm.set(new MenuState(gsm));
                dispose();
                touched = false;
            }
            else{
                dispose();
                generateTextures();
                touched = false;
            }
        }
    }

    @Override
    public void update(byte[] message) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, graphicsX, graphicsY);
        sb.draw(playAgainBtnImage, (graphicsX / 2) - (playAgainBtnX / 2), graphicsY / 2 - playAgainBtnY / 2, playAgainBtnX, playAgainBtnY);
        sb.draw(mainMenuBtnImage, (graphicsX/ 2) - (mainMenuBtnX/2), graphicsY/2 - playAgainBtnY/2 - mainMenuBtnY - bufferFromTop, mainMenuBtnX, mainMenuBtnY);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        mainMenuBtnImage.dispose();
        playAgainBtnImage.dispose();
    }
    public void generateTextures(){
        background = new Texture("menu_bg.png");
        playAgainBtnImage = new Texture("playAgainBtn.png");
        mainMenuBtnImage = new Texture("mainMenuBtn.png");
    }
}