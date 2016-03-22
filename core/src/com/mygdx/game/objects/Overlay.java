package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Samuel on 21/3/2016.
 */
public class Overlay extends GameObject{
    public Overlay(int y){
        super();
        this.setImage(new Texture(Gdx.files.internal("effects1.png")));
        this.x = 0;
        this.y = y;
        this.width = 480;
        this.height = 800;
    }
}