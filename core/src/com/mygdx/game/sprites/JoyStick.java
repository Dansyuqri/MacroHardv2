package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Syuqri on 3/7/2016.
 */
public class JoyStick {
    private Vector3 joystickPos;
    private Vector3 joystickCenterPos;
    private Texture joystickImage;
    private Texture joystickCentreImage;
    private Rectangle joystick;
    private Rectangle joystickCentre;

    public JoyStick(int x, int y){
        joystickImage = new Texture(Gdx.files.internal("joystick.png"));
        joystickCentreImage = new Texture(Gdx.files.internal("joystick_centre.png"));
        joystickPos = new Vector3(x,y,0);
        joystickCenterPos = new Vector3(x,y,0);
        joystick = new Rectangle();
        joystick.height = 100;
        joystick.width = 100;

        joystickCentre = new Rectangle();
        joystickCentre.height = 21;
        joystickCentre.width = 21;
    }

    public Vector3 getJoystickPos() {
        return joystickPos;
    }

    public Texture getJoystickImage() {
        return joystickImage;
    }

    public Texture getJoystickCentreImage() {
        return joystickCentreImage;
    }

    public Vector3 getJoystickCenterPos() {
        return joystickCenterPos;
    }
}
