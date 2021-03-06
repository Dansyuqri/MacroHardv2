package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Syuqri on 3/7/2016.
 */
public class JoyStick {
    private Texture joystickImage;
    private Texture joystickCentreImage;
    private Rectangle joystick;
    private Rectangle joystickCentre;

    public JoyStick(){
        joystickImage = new Texture(Gdx.files.internal("joystick.png"));
        joystickCentreImage = new Texture(Gdx.files.internal("joystick_centre_stone.png"));
        joystick = new Rectangle();

        joystick.x = 385;
        joystick.y = 70;

        joystick.height = 135;
        joystick.width = 135;

        joystickCentre = new Rectangle();

        joystickCentre.height = 115;
        joystickCentre.width = 115;

        joystickCentre.x = joystick.x - joystickCentre.width/2;
        joystickCentre.y = joystick.y - joystickCentre.height/2;
    }
    public Texture getJoystickImage() {return joystickImage;}

    public Texture getJoystickCentreImage() {
        return joystickCentreImage;
    }
    public float getJoystickWidth(){return joystick.width;}
    public float getJoystickHeight(){return joystick.height;}
    public float getJoystickCenterWidth(){return joystickCentre.width;}
    public float getJoystickCenterHeight(){return joystickCentre.height;}

    public float getX(){
        return joystick.x;
    }
    public float getY(){
        return joystick.y;
    }
    public float getCX(){
        return joystickCentre.x;
    }
    public float getCY(){
        return joystickCentre.y;
    }
    public void setX(float x){
        joystick.x = x;
    }
    public void setY(float y){
        joystick.y = y;
    }
    public void setCX(float x){
        joystickCentre.x = x;
    }
    public void setCY(float y){
        joystickCentre.y = y;
    }

}
