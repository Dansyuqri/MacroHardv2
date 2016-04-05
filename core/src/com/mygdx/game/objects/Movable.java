package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;

/**
 * Created by Samuel on 23/3/2016.
 */
public abstract class Movable extends GameObject {
    protected Movable(float x, float y, float width, float height){
        super(x, y, width, height);
    }

    Movable(int x, int y, int width, int height){
        super(x, y, width, height);
    }
    public void scroll (float gameSpeed){
        y -= gameSpeed * Math.min(Gdx.graphics.getDeltaTime(), (float) 0.03);
    }
}
