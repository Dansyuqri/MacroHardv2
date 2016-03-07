package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Syuqri on 3/7/2016.
 */
public class Player {
    private Vector3 position;
    private Vector3 velocity;

    private Texture player;

    public Player(int x, int y){
        position = new Vector3(x,y,0);
        velocity = new Vector3(0,0,0);
        player = new Texture("bucket.png");
    }

    public void update(float dt){

    }

    public Vector3 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return player;
    }
    public void move(){

    }
}
