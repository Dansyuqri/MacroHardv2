package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Syuqri on 3/16/2016.
 */
public class Background extends Movable{
    public Background(int y){
        super();
        this.setImage(new Texture(Gdx.files.internal("bg.png")));
        this.x = 0;
        this.y = y;
        this.width = 480;
        this.height = 800;
    }
}
