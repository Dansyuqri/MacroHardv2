package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Player extends GameObject {
    private String passivePower;
    private String activePower;
    private Texture texture;
    public Player(){
        super();
        texture = new Texture(Gdx.files.internal("player_temp.png"));
        this.x = 480/2-50/2;
        this.y = 400;
        this.width = 40;
        this.height = 40;
    }

    public String getActivePower() {
        return activePower;
    }

    public void setActivePower(String activePower) {
        this.activePower = activePower;
    }

    public void setPassivePower(String power) {
        this.passivePower = power;
    }

    public String getPassivePower() {
        return passivePower;
    }

    public Texture getTexture(){
        return texture;
    }
}
