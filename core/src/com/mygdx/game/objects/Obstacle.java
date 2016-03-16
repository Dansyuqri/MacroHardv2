package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Obstacle extends GameObject {
    public Obstacle(){
        super();
        this.setImage(new Texture(Gdx.files.internal("wall4.1.png")));
    }
}