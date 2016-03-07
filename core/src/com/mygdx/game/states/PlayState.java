package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.sprites.JoyStick;
import com.mygdx.game.sprites.Player;

/**
 * Created by Syuqri on 3/7/2016.
 */
public class PlayState extends State{
    private Player player;
    private boolean touchHeld = false;
    private JoyStick joystick;

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        player = new Player(50,100);
        cam.setToOrtho(false, Gdx.graphics.getHeight()/2,Gdx.graphics.getWidth()/2);
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()){
            joystick = new JoyStick(Gdx.input.getX(),Gdx.input.getY());
            touchHeld = true;
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        player.update(dt);
        //joystick.update(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        if(touchHeld){
            sb.draw(joystick.getJoystickImage(), joystick.getJoystickPos().x, joystick.getJoystickPos().y);
            sb.draw(joystick.getJoystickCentreImage(), joystick.getJoystickCenterPos().x, joystick.getJoystickCenterPos().y);
        }
        sb.draw(player.getTexture(),player.getPosition().x,player.getPosition().y);
        sb.end();
    }

    @Override
    public void dispose() {

    }
}
