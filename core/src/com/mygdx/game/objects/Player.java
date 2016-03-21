package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Player extends GameObject {
    private String power;
    private Texture texture;
    public Player(){
        super();
        texture = new Texture(Gdx.files.internal("player_temp.png"));
        this.x = 480/2-50/2;
        this.y = 400;
        this.width = 40;
        this.height = 40;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getPower() {
        return power;
    }

    public Texture getTexture(){
        return texture;
    }
}
