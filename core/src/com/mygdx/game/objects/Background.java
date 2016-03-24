package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Syuqri on 3/16/2016.
 */
public class Background extends Movable{
    public Background(int y){
        super(0, y, 480, 800);
        this.setImage(new Texture(Gdx.files.internal("bg.png")));
    }
}
