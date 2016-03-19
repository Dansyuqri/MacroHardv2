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
    private Vector3 touchPos = new Vector3(0,0,0);

    public MenuState(GameStateManager gsm) {
        super(gsm);
        background = new Texture("menu_bg.png");
        playBtn = new Texture("playBtn.png");
        instructionBtn = new Texture("instructionBtn.png");

    }

    @Override
    public void handleInput() {
        touchPos.x = Gdx.input.getX();
        touchPos.y = Gdx.input.getY();
        System.out.println(touchPos.y);
        /****************************************************************************************
         * This is a set of conditions to handle the highlighting of the play button when pressed
         * **************************************************************************************
         */
        if(Gdx.input.isTouched()){
            if(touchPos.x<=(Gdx.graphics.getWidth()/2)+(playBtn.getWidth()*3/2) && touchPos.x>=(Gdx.graphics.getWidth()/2)-(playBtn.getWidth()*3/2)){
                if(touchPos.y<=(Gdx.graphics.getHeight()/2)+(playBtn.getHeight()*3/2) && touchPos.y>=(Gdx.graphics.getHeight()/2)-(playBtn.getHeight()*3/2)){
                    playBtn.dispose();
                    playBtn = new Texture("playBtn_pressed.png");
                }
            }
        }
        if(!Gdx.input.isTouched()){
            if(touchPos.x<=(Gdx.graphics.getWidth()/2)+(playBtn.getWidth()*3/2) && touchPos.x>=(Gdx.graphics.getWidth()/2)-(playBtn.getWidth()*3/2)){
                if(touchPos.y<=(Gdx.graphics.getHeight()/2)+(playBtn.getHeight()*3/2) && touchPos.y>=(Gdx.graphics.getHeight()/2)-(playBtn.getHeight()*3/2)){
                    System.out.println("This is menustate");
                    gsm.set(new PlayStateHost(gsm));
                    dispose();
                }
            }
        }
        if(!Gdx.input.isTouched()){
            playBtn.dispose();
            playBtn = new Texture("playBtn.png");
        }


        /***********************************************************************************************
         * This is a set of conditions to handle the highlighting of the instruction button when pressed
         * *********************************************************************************************
         */
/*        if(Gdx.input.isTouched()){
            if(touchPos.x<=(Gdx.graphics.getWidth()/2)+(instructionBtn.getWidth()*3/2) && touchPos.x>=(Gdx.graphics.getWidth()/2)-(instructionBtn.getWidth()*3/2)){
                if(touchPos.y<=(Gdx.graphics.getHeight()/2-playBtn.getHeight()*3/2-100+instructionBtn.getHeight()*3/2) && touchPos.y>=(Gdx.graphics.getHeight()/2-playBtn.getHeight()*3/2-100-instructionBtn.getHeight()*3/2)){
                    playBtn.dispose();
                    playBtn = new Texture("instructionBtn_pressed.png");
                }
            }
        }
        if(!Gdx.input.isTouched()){
            if(touchPos.x<=(Gdx.graphics.getWidth()/2)+(instructionBtn.getWidth()*3/2) && touchPos.x>=(Gdx.graphics.getWidth()/2)-(instructionBtn.getWidth()*3/2)){
                if(touchPos.y<=(Gdx.graphics.getHeight()/2-playBtn.getHeight()*3/2-100+instructionBtn.getHeight()*3/2) && touchPos.y>=(Gdx.graphics.getHeight()/2-playBtn.getHeight()*3/2-100-instructionBtn.getHeight()*3/2)){
                    System.out.println("This is menustate");
                    gsm.set(new PlayState(gsm));
                    dispose();
                }
            }
        }
        if(!Gdx.input.isTouched()){
            playBtn.dispose();
            playBtn = new Texture("instructionBtn.png");
        }*/
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sb.draw(playBtn,(Gdx.graphics.getWidth()/2)-(playBtn.getWidth()*3/2),Gdx.graphics.getHeight()/2,playBtn.getWidth()*3,playBtn.getHeight()*3);
        sb.draw(instructionBtn,(Gdx.graphics.getWidth()/2)-(instructionBtn.getWidth()*3/2),Gdx.graphics.getHeight()/2-playBtn.getHeight()*3/2-100,instructionBtn.getWidth()*3,instructionBtn.getHeight()*3);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        playBtn.dispose();
        instructionBtn.dispose();

    }
}