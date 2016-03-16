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
    private Rectangle player;
    public Player(){
        super();
        texture = new Texture(Gdx.files.internal("player_temp.png"));
        player = new Rectangle();
        player.x = 480 / 2 - 50 / 2; // center the player horizontally
        player.y = 400; // bottom left corner of the player is 400 pixels above the bottom screen edge
        player.width = 40;
        player.height = 40;

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
