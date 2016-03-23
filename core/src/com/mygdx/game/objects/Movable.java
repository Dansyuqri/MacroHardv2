package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;

/**
 * Created by Samuel on 23/3/2016.
 */
public abstract class Movable extends GameObject {
    public void scroll (float gameSpeed){
        y -= gameSpeed * Gdx.graphics.getDeltaTime();
    }
}
