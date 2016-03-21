package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by Syuqri on 3/7/2016.
 */
public class MenuState extends State{
    private Texture background;
    private Texture playBtn;
    private Texture instructionBtn;
    private Texture quickGameBtn;
    private Vector3 touchPos = new Vector3(0,0,0);
    private float bufferFromBottom = 200;

    //Resize variables
    private float playBtnX, playBtnY, instructionBtnX,instructionBtnY, quickGameBtnX, quickGameBtnY, graphicsX, graphicsY;
    public MenuState(GameStateManager gsm) {
        super(gsm);
        background = new Texture("menu_bg.png");
        playBtn = new Texture("playBtn.png");
        instructionBtn = new Texture("instructionBtn.png");
        quickGameBtn = new Texture("quickGameBtn.png");

        playBtnX = playBtn.getWidth()*3;
        playBtnY = playBtn.getHeight()*3;

        instructionBtnX = instructionBtn.getWidth()*3;
        instructionBtnY = instructionBtn.getHeight()*3;


        quickGameBtnX = quickGameBtn.getWidth()*3;
        quickGameBtnY = quickGameBtn.getHeight()*3;

        graphicsX = Gdx.graphics.getWidth();
        graphicsY = Gdx.graphics.getHeight();

    }

    @Override
    public void handleInput() {
        touchPos.x = Gdx.input.getX();
        touchPos.y = Gdx.input.getY();
        //System.out.println(touchPos.y);
        /****************************************************************************************
         * This is a set of conditions to handle the highlighting of the play button when pressed
         * **************************************************************************************
         */
        if(Gdx.input.isTouched()){
            if(touchPos.x<=(graphicsX/2)+(playBtnX/2) && touchPos.x>=(graphicsX/2)-(playBtnX/2)){
                if(touchPos.y<=(graphicsY/2)+(playBtnY/2) && touchPos.y>=(graphicsY/2)-(playBtnY/2)){
                    playBtn.dispose();
                    playBtn = new Texture("playBtn_pressed.png");
                }
            }
        }
        else if(!Gdx.input.isTouched()){
            if(touchPos.x<=(graphicsX/2)+(playBtnX/2) && touchPos.x>=(graphicsX/2)-(playBtnX/2)){
                if(touchPos.y<=(graphicsY/2)+(playBtnY/2) && touchPos.y>=(graphicsY/2)-(playBtnY/2)){
                    System.out.println("This is menustate");
                    gsm.set(new PlayStateHost(gsm));
                    dispose();
                }
            }
            else{
                playBtn.dispose();
                playBtn = new Texture("playBtn.png");
            }
        }



        /***********************************************************************************************
         * This is a set of conditions to handle the highlighting of the instruction button when pressed
         * *********************************************************************************************
         */
        if(Gdx.input.isTouched()){
            if(touchPos.x<=(graphicsX/2)+(instructionBtnX/2) && touchPos.x>=(graphicsX/2)-(instructionBtnX/2)){
                if(touchPos.y<=(graphicsY/2+playBtnY/2+bufferFromBottom) && touchPos.y>=(graphicsY/2+playBtnY/2+bufferFromBottom-instructionBtnY/2)){
                    instructionBtn.dispose();
                    instructionBtn = new Texture("instructionBtn_pressed.png");
                }
            }
        }
        else if(!Gdx.input.isTouched()){
            if(touchPos.x<=(graphicsX/2)+(instructionBtnX/2) && touchPos.x>=(graphicsX/2)-(instructionBtnX/2)){
                if(touchPos.y<=(graphicsY/2+playBtnY/2+bufferFromBottom) && touchPos.y>=(graphicsY/2+playBtnY/2+bufferFromBottom-instructionBtnY/2)){
                    System.out.println("This is menustate");
                    gsm.set(new InstructionState(gsm));
                    dispose();
                }
            }
            else{
                instructionBtn.dispose();
                instructionBtn = new Texture("instructionBtn.png");
            }
        }

        /***********************************************************************************************
         * This is a set of conditions to handle the highlighting of the quick game button when pressed
         * *********************************************************************************************
         */
        if(Gdx.input.isTouched()){
            if(touchPos.x<=(graphicsX/2)+(quickGameBtnX/2) && touchPos.x>=(graphicsX/2)-(quickGameBtnX/2)){
                if(touchPos.y<=(graphicsY/2+playBtnY/2+instructionBtnY+bufferFromBottom) && touchPos.y>=(graphicsY/2+playBtnY/2+instructionBtnY+bufferFromBottom-quickGameBtnY/2)){
                    quickGameBtn.dispose();
                    quickGameBtn = new Texture("quickGameBtn_pressed.png");
                }
            }
        }
        else if(!Gdx.input.isTouched()){
            if(touchPos.x<=(graphicsX/2)+(quickGameBtnX/2) && touchPos.x>=(graphicsX/2)-(quickGameBtnX/2)){
                if(touchPos.y<=(graphicsY/2+playBtnY/2+instructionBtnY+bufferFromBottom) && touchPos.y>=(graphicsY/2+playBtnY/2+instructionBtnY+bufferFromBottom-quickGameBtnY/2)){
                    //TODO: put your method call here (ryan)
                    quickGameBtn.dispose();
                    quickGameBtn = new Texture("quickGameBtn.png");
                }
            }
            else{
                quickGameBtn.dispose();
                quickGameBtn = new Texture("quickGameBtn.png");
            }
        }

    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, graphicsX, graphicsY);
        sb.draw(playBtn,(graphicsX/2)-(playBtnX/2),graphicsY/2-playBtnY/2,playBtnX,playBtnY);
        sb.draw(instructionBtn, (graphicsX/ 2) - (instructionBtnX/2), graphicsY/2 - playBtnY/2 - bufferFromBottom, instructionBtnX, instructionBtnY);
        sb.draw(quickGameBtn,(graphicsX/2)-(quickGameBtnX/2),(graphicsY/2 - playBtnY/2 - instructionBtnY- bufferFromBottom),quickGameBtnX,quickGameBtnY);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        playBtn.dispose();
        instructionBtn.dispose();
        quickGameBtn.dispose();

    }
}