package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Samuel on 21/3/2016.
 */
public class Overlay extends Movable{
    public Overlay(float y){
        super(0, y, 480, 200);
        this.setImage(new Texture(Gdx.files.internal("effects1.png")));
    }
}