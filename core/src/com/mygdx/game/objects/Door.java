package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Door extends Obstacle {
    public Door(){
        super();
        this.setImage(new Texture(Gdx.files.internal("gate_closed.png")));
    }
}
