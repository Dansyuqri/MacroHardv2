package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Samuel on 22/3/2016.
 */
public class UI extends GameObject{
    public UI(int y){
        super(0, y, 480, 800);
        this.setImage(new Texture(Gdx.files.internal("UI_left.png")));
    }
}
