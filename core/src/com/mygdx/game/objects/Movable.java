package com.mygdx.game.objects;

import com.mygdx.game.states.PlayState;

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
        y -= gameSpeed * PlayState.deltaCap;
    }
}
