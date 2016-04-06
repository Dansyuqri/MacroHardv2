package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.objects.CustomButton;

/**
 * Created by Syuqri on 3/23/2016.
 */
public class RestartState extends State{
    private Texture background, playAgainBtnImage, mainMenuBtnImage;
    private CustomButton mainMenuBtn;

    private Vector3 touchPos = new Vector3(0,0,0);
    private boolean touched = false;
    public BitmapFont font;
    //Resize variables
    private float graphicsX, graphicsY,
            mainMenuBtnX, mainMenuBtnY;

    private int score;

    public RestartState(GameStateManager gsm, int score){
        super(gsm);
        this.score = score;
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        graphicsX = Gdx.graphics.getWidth();
        graphicsY = Gdx.graphics.getHeight();
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("SF Atarian System.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 200;
        font = fontGenerator.generateFont(parameter); // font size 12 pixels
        generateTextures();


        mainMenuBtnX = mainMenuBtnImage.getWidth()*3;
        mainMenuBtnY = mainMenuBtnImage.getHeight()*3;
        mainMenuBtn = new CustomButton((graphicsX/ 2) - (mainMenuBtnX/2), graphicsY/2 - mainMenuBtnY/2, mainMenuBtnX, mainMenuBtnY);
        mainMenuBtn.setImage(mainMenuBtnImage);

    }
    @Override
    protected void handleInput() {
        touchPos.x = Gdx.input.getX();
        touchPos.y = Gdx.input.getY();
        cam.unproject(touchPos);

        /****************************************************************************************
         * This is a set of conditions to handle the highlighting of the buttons when pressed
         * **************************************************************************************
         */
        if (Gdx.input.isTouched() && !touched){
             if(mainMenuBtn.contains(touchPos.x,touchPos.y)){
                mainMenuBtnImage.dispose();
                mainMenuBtnImage = new Texture("mainMenuBtn_pressed.png");
                touched = true;
            }
        }

        else if(!Gdx.input.isTouched() && touched){
             if(mainMenuBtn.contains(touchPos.x,touchPos.y)){
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
    public void update(byte[] message) {}

    @Override
    public void render(SpriteBatch sb) {
        GlyphLayout layoutScoreText = new GlyphLayout(font,"Your Score");
        GlyphLayout layoutScore = new GlyphLayout(font,""+score);
        handleInput();
        sb.begin();
        cam.update();
        sb.setProjectionMatrix(cam.combined);
        sb.draw(background, 0, 0, graphicsX, graphicsY);
        font.setColor(Color.WHITE);
        font.draw(sb, "Your Score: ", graphicsX/2-layoutScoreText.width/2, graphicsY*3/4);
        font.draw(sb, ""+score, graphicsX/2-layoutScore.width/2, graphicsY*3/4-layoutScoreText.height-layoutScore.height);
        sb.draw(mainMenuBtnImage, (graphicsX/ 2) - (mainMenuBtnX/2), graphicsY/2 - mainMenuBtnY/2, mainMenuBtnX, mainMenuBtnY);
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